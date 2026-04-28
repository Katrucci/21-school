const soundFiles = [
    '/sounds/start.wav',
    '/sounds/start1.wav',
    '/sounds/start2.wav',
    '/sounds/start3.wav',
    '/sounds/start4.wav',
    '/sounds/start5.wav',
    '/sounds/start6.wav',
    '/sounds/start7.wav',
    '/sounds/start8.wav',
    '/sounds/start9.wav'
];

let currentGameId = null;
let currentUserId = null;
let isPlayerX = false;

let isMakingMove = false;
let currentGameStatus = null;
let currentGameCells = null;

// Проверяем авторизацию
const authHeader = localStorage.getItem('authHeader') || sessionStorage.getItem('authHeader');
const userLogin = localStorage.getItem('userLogin') || sessionStorage.getItem('userLogin');
currentUserId = localStorage.getItem('userId') || sessionStorage.getItem('userId');

if (!authHeader || !userLogin) {
    window.location.href = '/login.html';
}

// Отображаем имя пользователя
const titleElement = document.querySelector('header h1');
const usernameDisplay = document.getElementById('username-display');
if (usernameDisplay && userLogin) {
    usernameDisplay.textContent = `${userLogin}`;
}

// Обработчик выбора режима игры
document.querySelectorAll('input[name="game-mode"]').forEach(radio => {
    radio.addEventListener('change', (e) => {
        const isPvP = e.target.value === 'PVP';
        document.getElementById('join-game-btn').style.display = isPvP ? 'inline-block' : 'none';
        document.getElementById('start-btn').textContent = isPvP ? 'Создать новую игру' : 'Начать новую игру';

        if (isPvP) {
            loadAvailableGames();
            document.getElementById('available-games-section').style.display = 'block';
        } else {
            document.getElementById('available-games-section').style.display = 'none';
        }
    });
});

// Загрузка списка доступных игр
async function loadAvailableGames() {
    try {
        const response = await fetch('/game/available', {
            headers: { 'Authorization': authHeader }
        });

        if (response.ok) {
            const games = await response.json();
            displayAvailableGames(games);
        }
    } catch (error) {
        console.error("Ошибка загрузки доступных игр:", error);
    }
}

// Отображение списка доступных игр
function displayAvailableGames(games) {
    const container = document.getElementById('available-games-list');

    if (games.length === 0) {
        container.innerHTML = '<p class="no-games">Нет доступных игр. Создайте новую!</p>';
        return;
    }

    container.innerHTML = games.map(game => `
        <div class="game-item">
            <span>Игра #${game.id.substring(0, 8)}</span>
            <span>Игрок X: ${game.playerXId ? "<img src='/image/oo.png' alt='👤' style='width: 32px; height: 32px; vertical-align: middle; margin-left: 8px;'>"
            : 'Ожидание...'}</span>
            <button onclick="joinGame('${game.id}')" class="btn-small">Присоединиться</button>
        </div>
    `).join('');
}

// Присоединение к игре
async function joinGame(gameId) {
    try {
        const response = await fetch(`/game/${gameId}/join`, {
            method: 'POST',
            headers: {
                'Authorization': authHeader,
                'Content-Type': 'application/json'
            }
        });

        if (response.ok) {
            const game = await response.json();
            currentGameId = game.id;
            isPlayerX = false; // Присоединившийся игрок всегда играет за O

            document.getElementById('game-board').style.display = 'grid';
            document.getElementById('available-games-section').style.display = 'none';
            document.getElementById('game-id-display').style.display = 'block';
            document.getElementById('game-id').textContent = currentGameId;

            renderBoard(game.board.cells, game.status, game.winner);
            startGamePolling();
             setTimeout(() => {
                            refreshGameState(); // Обновляем состояние у текущего игрока
                        }, 500);
        } else {
            alert('Не удалось присоединиться к игре');
        }
    } catch (error) {
        console.error("Ошибка при присоединении к игре:", error);
    }
}

// Создание новой игры
document.getElementById('start-btn').addEventListener('click', async () => {
    const selectedMode = document.querySelector('input[name="game-mode"]:checked').value;
    const isPvE = selectedMode === 'PVE';

    // Проигрываем случайный звук
    const randomIndex = Math.floor(Math.random() * soundFiles.length);
    const randomSound = new Audio(soundFiles[randomIndex]);
    randomSound.play().catch(error => {
        console.log("Звук не смог воспроизвестись:", error);
    });

    try {
        // Добавляем параметр mode в URL
        const url = `/game/create?mode=${selectedMode}`;
        const response = await fetch(url, {
            method: 'POST',
            headers: {
                'Authorization': authHeader,
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const game = await response.json();

        currentGameId = game.id;
        isPlayerX = true; // Создатель всегда играет за X

        document.getElementById('game-board').style.display = 'grid';
document.getElementById('restart-btn').style.display = 'none';
        document.getElementById('available-games-section').style.display = 'none';
        document.getElementById('game-id-display').style.display = 'block';
        document.getElementById('game-id').textContent = currentGameId;

        renderBoard(game.board.cells, game.status, game.winner);

        // Если это PvP игра, начинаем polling для отслеживания ходов соперника
        if (!isPvE) {
            startGamePolling();
        }
    } catch (error) {
        console.error("Не удалось создать игру:", error);
        alert('Ошибка при создании игры: ' + error.message);
    }
});



// Polling для PvP игр (проверка обновлений)
let pollingInterval = null;
function startGamePolling() {
    if (pollingInterval) clearInterval(pollingInterval);

    pollingInterval = setInterval(async () => {
        if (!currentGameId) return;

        try {
            const response = await fetch(`/game/${currentGameId}`, {
                headers: { 'Authorization': authHeader }
            });

            if (response.ok) {
                const game = await response.json();
                renderBoard(game.board.cells, game.status, game.winner);

                // Если игра завершена, останавливаем polling
                if (['X_WON', 'O_WON', 'DRAW'].includes(game.status)) {
                    clearInterval(pollingInterval);
                    pollingInterval = null;
                }
            }
        } catch (error) {
            console.error("Ошибка polling:", error);
        }
    }, 1000); // Проверяем каждые 2 секунды
}

// Копирование ID игры
document.getElementById('copy-game-id')?.addEventListener('click', () => {
    const gameId = document.getElementById('game-id').textContent;
    navigator.clipboard.writeText(gameId).then(() => {
        alert('ID игры скопирован!');
    });
});

// Кнопка "Присоединиться к игре по ID"
document.getElementById('join-game-btn').addEventListener('click', () => {
    const gameId = prompt('Введите ID игры:');
    if (gameId) {
        joinGame(gameId);
    }
});

// Выход из системы
document.getElementById('logout-btn').addEventListener('click', () => {
    localStorage.removeItem('authHeader');
    localStorage.removeItem('userId');
    localStorage.removeItem('userLogin');
    sessionStorage.clear();
    window.location.href = '/login.html';
});

// Перезапуск игры
document.getElementById('restart-btn').addEventListener('click', async () => {
    if (!currentGameId) return;

    try {
        const response = await fetch(`/game/${currentGameId}/restart`, {
            method: 'POST',
            headers: { 'Authorization': authHeader }
        });

        if (response.ok) {
            const game = await response.json();
            renderBoard(game.board.cells, game.status, game.winner);

            // Возобновляем polling для PvP
            const isPvE = document.querySelector('input[name="game-mode"]:checked').value === 'PVE';
            if (!isPvE) {
                startGamePolling();
            }
        }
    } catch (error) {
        console.error("Ошибка при перезапуске:", error);
    }
});

// Выполнение хода
async function makeMove(row, col) {
    if (!currentGameId) return;

    try {
       const response = await fetch(`/game/${currentGameId}/move`, {
            method: 'POST',
            headers: {
                'Authorization': authHeader,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ row, col })
        });

            if (response.status === 409) {  // CONFLICT
                    // Данные устарели - принудительно обновляем
                    console.warn("Конфликт версий, обновляем данные...");
                    await refreshGameState();
                    return;
                }

        if (!response.ok) {
            const error = await response.json();
            alert(error.message || "Ошибка при выполнении хода");
            return;
        }

        const updatedGame = await response.json();
        renderBoard(updatedGame.board.cells, updatedGame.status, updatedGame.winner);

        // Сохраняем ID игроков для определения победителя
        if (updatedGame.playerXId) {
            localStorage.setItem('playerXId', updatedGame.playerXId);
        }
        if (updatedGame.playerOId) {
            localStorage.setItem('playerOId', updatedGame.playerOId);
        }
    } catch (error) {
        console.error("Сетевая ошибка:", error);
    }
}


async function refreshGameState() {
    if (!currentGameId) return;

    try {
        const response = await fetch(`/game/${currentGameId}`, {
            headers: { 'Authorization': authHeader }
        });

        if (response.ok) {
            const game = await response.json();
            renderBoard(game.board.cells, game.status, game.winner);
            if (game.playerXId) localStorage.setItem('playerXId', game.playerXId);
            if (game.playerOId) localStorage.setItem('playerOId', game.playerOId);
        }
    } catch (error) {
        console.error("Ошибка обновления состояния игры:", error);
    }
}


// Отрисовка доски
function renderBoard(cells, status, winner) {
    const boardElement = document.getElementById('game-board');
    const statusElement = document.getElementById('status');
    const restartBtn = document.getElementById('restart-btn');

    boardElement.innerHTML = '';

    // Определяем, чей сейчас ход
    const isMyTurn = checkIfMyTurn(status);
      const gameStatus = status;
     const isTerminal = ['X_WON', 'O_WON', 'DRAW'].includes(gameStatus);

    cells.forEach((rowArr, r) => {
        rowArr.forEach((val, c) => {
            const cell = document.createElement('div');
            cell.className = 'cell';

    if (val === 1) {
        cell.innerHTML = "<img src='/image/Pu2.png' alt='🔥' style='width: 48px; height: 48px; vertical-align: middle; margin-left: 8px;'>";
        cell.classList.add('x');
    } else if (val === 2) {
        cell.innerHTML = "<img src='/image/crown2.png' alt='👑' style='width: 48px; height: 48px; vertical-align: middle; margin-left: 8px;'>";
        cell.classList.add('o');
    }

            // Блокируем клик, если игра завершена или не ваша очередь

   cell.onclick = () => {
       if (val === 0 && !isTerminal && isMyTurn) {
           makeMove(r, c);
       } else if (!isMyTurn && !isTerminal) {
           alert('Сейчас не ваш ход!');
       }
   };

            boardElement.appendChild(cell);
        });
    });

    // Обновляем статус
    updateStatus(status, winner);
    restartBtn.style.display = isTerminal ? 'block' : 'none';
}

// Проверка, чей сейчас ход
function checkIfMyTurn(status) {
    if (status !== 'IN_PROGRESS') return false;

    const cells = document.querySelectorAll('.cell');
    const xCount = Array.from(cells).filter(cell => cell.textContent === 'X').length;
    const oCount = Array.from(cells).filter(cell => cell.textContent === 'O').length;

    // X ходит первым, когда количество X равно количеству O
    // O ходит вторым, когда X на один больше чем O
    if (isPlayerX) {
        return xCount === oCount;
    } else {
        return xCount > oCount;
    }
}

// Обновление текста статуса
function updateStatus(status, winner) {
    const statusElement = document.getElementById('status');

    if (['X_WON', 'O_WON', 'DRAW'].includes(status)) {
        statusElement.classList.add('finished');

        if (status === 'DRAW') {
            statusElement.innerHTML = "НИЧЬЯ! <img src='/image/toadstool.png' alt='🤝' style='width: 48px; height: 48px; vertical-align: middle; margin-left: 8px;'>";
        } else if ((winner === 'X' && isPlayerX) || (winner === 'O' && !isPlayerX)) {
            statusElement.innerHTML = "ВЫ ПОБЕДИЛИ! <img src='/image/Pu.png' alt='🔥' style='width: 48px; height: 48px; vertical-align: middle; margin-left: 8px;'>";
        } else {
            statusElement.innerHTML = "ПОБЕДИЛ СОПЕРНИК! <img src='/image/crown.png' alt='👑' style='width: 48px; height: 48px; vertical-align: middle; margin-left: 8px;'>";
        }
    } else if (status === 'WAITING') {
        statusElement.classList.remove('finished');
        statusElement.innerHTML = "Ожидание соперника... <img src='/image/white.png' alt='⚠️' style='width: 48px; height: 48px; vertical-align: middle; margin-left: 8px;'>";
    } else {
        statusElement.classList.remove('finished');
        const isMyTurn = checkIfMyTurn(status);
        statusElement.innerHTML = isMyTurn ? "ВАШ ХОД! " : "ХОД СОПЕРНИКА... ";
    }
}

// Очистка polling при уходе со страницы
window.addEventListener('beforeunload', () => {
    if (pollingInterval) {
        clearInterval(pollingInterval);
    }
});