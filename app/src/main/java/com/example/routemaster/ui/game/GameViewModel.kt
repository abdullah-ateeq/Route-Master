package com.example.routemaster.ui.game

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.routemaster.audio.HapticController
import com.example.routemaster.audio.SoundController
import com.example.routemaster.data.GamePreferencesRepository
import com.example.routemaster.data.GameSettings
import com.example.routemaster.engine.RouteGraph
import com.example.routemaster.engine.TileBag
import com.example.routemaster.model.BoardConstants
import com.example.routemaster.model.GridPosition
import com.example.routemaster.model.RouteCell
import com.example.routemaster.model.RouteTile
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = GamePreferencesRepository(application)
    val soundController = SoundController()
    val hapticController = HapticController(application)

    val settings: StateFlow<GameSettings> = repository.settingsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = GameSettings()
    )

    private val _gameState = MutableStateFlow(RouteMasterState())
    val gameState: StateFlow<RouteMasterState> = _gameState.asStateFlow()

    private val undoStack = mutableListOf<RouteMasterState>()
    private var initialDeck: List<RouteTile> = emptyList()

    private var timerJob: Job? = null
    private var deltaClearJob: Job? = null

    init {
        // Sync sound & haptics with user settings
        viewModelScope.launch {
            settings.collect { s ->
                soundController.isEnabled = s.soundEffects
                hapticController.isEnabled = s.hapticFeedback
            }
        }
        startNewGame()
    }

    fun startNewGame() {
        undoStack.clear()
        deltaClearJob?.cancel()

        val deck = TileBag.generateDeck()
        initialDeck = deck.toList()

        val (tray, bag) = TileBag.dealInitial(deck)
        val initialBoard = BoardConstants.createInitialBoard()

        _gameState.value = RouteMasterState(
            board = initialBoard,
            tray = tray,
            bag = bag,
            selectedTileId = tray.firstOrNull()?.id,
            moveCount = 0,
            longestRouteLength = 0,
            longestRouteCells = emptySet(),
            lastPlacedPosition = null,
            elapsedSeconds = 0L,
            isFinished = false,
            isAnimating = false,
            newRouteDelta = null
        )

        startTimer()
    }

    fun restartGame() {
        undoStack.clear()
        deltaClearJob?.cancel()

        // Restart with the exact starting board and tile supply of this game
        val deckToUse = if (initialDeck.isNotEmpty()) initialDeck else TileBag.generateDeck()
        val (tray, bag) = TileBag.dealInitial(deckToUse)
        val initialBoard = BoardConstants.createInitialBoard()

        _gameState.value = RouteMasterState(
            board = initialBoard,
            tray = tray,
            bag = bag,
            selectedTileId = tray.firstOrNull()?.id,
            moveCount = 0,
            longestRouteLength = 0,
            longestRouteCells = emptySet(),
            lastPlacedPosition = null,
            elapsedSeconds = 0L,
            isFinished = false,
            isAnimating = false,
            newRouteDelta = null
        )

        startTimer()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000L)
                if (!_gameState.value.isFinished) {
                    _gameState.update { it.copy(elapsedSeconds = it.elapsedSeconds + 1) }
                }
            }
        }
    }

    fun selectTile(tile: RouteTile) {
        val current = _gameState.value
        if (current.isFinished || current.isAnimating) return

        if (current.selectedTileId != tile.id) {
            _gameState.update { it.copy(selectedTileId = tile.id) }
            soundController.play(SoundController.SoundEffect.TILE_SELECT)
            hapticController.trigger(HapticController.HapticType.SELECTION)
        }
    }

    fun rotateSelectedTile() {
        val current = _gameState.value
        if (current.isFinished || current.isAnimating) return
        val selectedId = current.selectedTileId ?: return

        val updatedTray = current.tray.map { tile ->
            if (tile.id == selectedId) tile.rotateClockwise() else tile
        }

        _gameState.update { it.copy(tray = updatedTray) }
        soundController.play(SoundController.SoundEffect.ROTATE)
        hapticController.trigger(HapticController.HapticType.ROTATION)
    }

    fun placeTile(position: GridPosition) {
        val current = _gameState.value

        // 1. Verify game active
        if (current.isFinished || current.isAnimating) return

        // 2. Verify selected tile exists
        val selectedTile = current.tray.find { it.id == current.selectedTileId } ?: return

        // 3. Verify destination within bounds & empty (not blocked, not already occupied)
        if (!position.isValid(BoardConstants.GRID_SIZE, BoardConstants.GRID_SIZE)) return
        val targetCell = current.board[position.row][position.col]
        if (targetCell !is RouteCell.Empty) return

        // 4. Save Undo snapshot
        undoStack.add(current.copy(isAnimating = false, newRouteDelta = null))

        // 5. Lock input
        _gameState.update { it.copy(isAnimating = true) }

        // 6. Place selected tile on board
        val newBoard = current.board.mapIndexed { r, rowList ->
            if (r == position.row) {
                rowList.mapIndexed { c, cell ->
                    if (c == position.col) RouteCell.Tile(selectedTile) else cell
                }
            } else {
                rowList
            }
        }

        // 7. Remove tile from tray
        val remainingTray = current.tray.filter { it.id != selectedTile.id }.toMutableList()

        // 8. Draw replacement from bag if available
        var remainingBag = current.bag
        if (remainingBag.isNotEmpty()) {
            val drawnTile = remainingBag.first()
            remainingTray.add(drawnTile)
            remainingBag = remainingBag.drop(1)
        }

        // 9. Recalculate connected components and longest route
        val previousLongest = current.longestRouteLength
        val newLongestRoute = RouteGraph.findLongestRoute(newBoard)
        val newLongestLength = newLongestRoute.size
        val hasRouteGrown = newLongestLength > previousLongest
        val delta = if (hasRouteGrown) newLongestLength - previousLongest else null

        // 10. Check if tile connected to any neighbor
        val connectedNeighbors = RouteGraph.getConnectedNeighbors(newBoard, position)
        val isConnectedToNeighbor = connectedNeighbors.isNotEmpty()

        // Play appropriate sound & haptics
        if (hasRouteGrown) {
            soundController.play(SoundController.SoundEffect.NEW_LONGEST_ROUTE)
            hapticController.trigger(HapticController.HapticType.NEW_LONGEST_ROUTE)
        } else if (isConnectedToNeighbor) {
            soundController.play(SoundController.SoundEffect.ROUTE_EXTENDED)
            hapticController.trigger(HapticController.HapticType.PLACEMENT)
        } else {
            soundController.play(SoundController.SoundEffect.TILE_PLACED)
            hapticController.trigger(HapticController.HapticType.PLACEMENT)
        }

        // 11. Select next tray tile automatically if available
        val nextSelectedId = remainingTray.firstOrNull()?.id

        // 12. Check game end
        val playableRemaining = RouteGraph.countEmptyPlayableCells(newBoard)
        val isGameComplete = (remainingTray.isEmpty() && remainingBag.isEmpty()) || (playableRemaining == 0)

        _gameState.update {
            it.copy(
                board = newBoard,
                tray = remainingTray,
                bag = remainingBag,
                selectedTileId = nextSelectedId,
                moveCount = it.moveCount + 1,
                longestRouteLength = newLongestLength,
                longestRouteCells = newLongestRoute,
                lastPlacedPosition = position,
                isFinished = isGameComplete,
                isAnimating = false,
                newRouteDelta = delta
            )
        }

        // Auto-clear route delta banner after 1.8s
        if (delta != null) {
            deltaClearJob?.cancel()
            deltaClearJob = viewModelScope.launch {
                delay(1800L)
                _gameState.update { it.copy(newRouteDelta = null) }
            }
        }

        // 13. If game completed, record stats and trigger victory feedback
        if (isGameComplete) {
            onGameCompleted()
        }
    }

    private fun onGameCompleted() {
        val state = _gameState.value
        soundController.play(SoundController.SoundEffect.GAME_COMPLETE)
        hapticController.trigger(HapticController.HapticType.GAME_COMPLETE)

        viewModelScope.launch {
            repository.recordGameResult(
                routeLength = state.longestRouteLength,
                coveragePercent = state.boardCoveragePercent,
                moves = state.moveCount,
                durationSeconds = state.elapsedSeconds
            )
        }
    }

    val canUndo: Boolean
        get() = undoStack.isNotEmpty() && !_gameState.value.isAnimating

    fun undo() {
        if (!canUndo) return
        val previous = undoStack.removeAt(undoStack.lastIndex)
        _gameState.value = previous.copy(
            isAnimating = false,
            newRouteDelta = null,
            lastPlacedPosition = null
        )
        soundController.play(SoundController.SoundEffect.ROTATE)
        hapticController.trigger(HapticController.HapticType.ROTATION)
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        deltaClearJob?.cancel()
    }
}
