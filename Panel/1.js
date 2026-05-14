const engineBtn = document.getElementById('engineBtn');
const airlockBtn = document.getElementById('airlockBtn');
const emergencyBtn = document.getElementById('emergencyBtn');
const coverTop = document.getElementById('coverTop');
const brightnessRange = document.getElementById('brightnessRange');
const engineFill = document.getElementById('engineFill');
const powerRange = document.getElementById('powerRange');
const brightnessFill = document.getElementById('brightnessFill');
const engineValue = document.getElementById('engineValue');
const brightnessValue = document.getElementById('brightnessValue');
const autoPilotToggle = document.getElementById('autoPilotToggle');
const tempValue = document.getElementById('tempValue');
const statusLight = document.getElementById('statusLight');
const statusMessage = document.getElementById('statusMessage');
const lowBatteryLight = document.getElementById('lowBatteryLight');

let engineRunning = false;
let airlockOpen = false;
let emergencyActive = false;
let temperature = 22;
let temperatureInterval = null;
let currentPower = 75;
let autoPilotActive = false;           // Флаг автопилота

// ========== ПЕРЕМЕННЫЕ ДЛЯ ЛОГИКИ ТЕМПЕРАТУРЫ ==========
let previousPower = 75;                // Для гистерезиса на 50%
let engineWasRunning = false;          // Для разового нагрева при включении
let lastWarningTime = 0;               // Для защиты от спама сообщений
let lastOrangeWarningTime = 0;         // Для защиты от спама оранжевого сообщения
let lastPower = 75;                    // Запоминаем последнее положение ползунка для определения направления
let heatingPhase = 0;  // 0 = нет активного движения, >0 = осталось секунд быстрого изменения
let lastPowerValue = currentPower;
let lastBrightnessValue = 60;

// ========== ПЕРЕМЕННЫЕ ДЛЯ ВЫКЛЮЧЕННОГО ДВИГАТЕЛЯ ==========
let engineOffTimer = null;
let engineOffStartTime = null;

function showMessage(text, type = 'success') {
    statusMessage.textContent = text;
    statusMessage.className = 'status-message ' + type;
    statusMessage.classList.add('show');

    if (type === 'error') {
        playDangerSound();
    }

    setTimeout(() => {
        statusMessage.classList.remove('show');
        if (type === 'error') {
            stopDangerSound();   // Останавливаем звук, когда сообщение исчезает
        }
    }, 3000);
}

// ========== ФУНКЦИЯ ОБНОВЛЕНИЯ МОЩНОСТИ НА ЭКРАНЕ ==========
function updatePowerUI() {
    engineFill.style.width = currentPower + '%';
    engineValue.textContent = currentPower;

    // Если двигатель выключен – всегда зелёная полоса
    if (!engineRunning) {
        engineFill.style.background = 'var(--secondary-text)';
        engineFill.style.boxShadow = '0 0 6px rgba(80, 255, 16, 0.3)';
        return;
    }

    if (currentPower > 80) {
        engineFill.style.background = 'linear-gradient(90deg, #ff4444, #ff0000)';
        engineFill.style.boxShadow = '0 0 6px rgba(255, 0, 0, 0.3)';
    } else {
        engineFill.style.background = 'var(--secondary-text)';
        engineFill.style.boxShadow = '0 0 6px rgba(80, 255, 16, 0.3)';
    }
}

// ========== ФУНКЦИЯ УСТАНОВКИ МОЩНОСТИ (С СИНХРОНИЗАЦИЕЙ) ==========
function setPower(value) {
    value = Math.min(100, Math.max(0, value));
    lastPower = currentPower; // Запоминаем старое перед обновлением
    currentPower = value;
    powerRange.value = value;
    updatePowerUI();

    // Если мощность изменилась, включаем фазу активного изменения температуры
    if (lastPower !== currentPower) {
        heatingPhase = 3;
    }
}

// ========== ФУНКЦИЯ ОБНОВЛЕНИЯ ТЕМПЕРАТУРЫ (ГЛАВНАЯ ЛОГИКА) ==========
function updateTemperature() {
    const tempElement = document.getElementById('tempValue');
    if (!tempElement) return;

    // ========== АВТОПИЛОТ: ПРИНУДИТЕЛЬНО СТАВИМ МОЩНОСТЬ 75% ==========
    if (autoPilotActive) {
        if (currentPower !== 75) {
            setPower(75);
        }
        // Температура стремится к 30°C (правила 7-8 из матрицы)
        if (temperature < 30) {
            temperature += 0.3;
            if (temperature > 30) temperature = 30;
        } else if (temperature > 30) {
            temperature -= 0.3;
            if (temperature < 30) temperature = 30;
        }

        // Обновляем отображение и выходим (остальная логика не нужна)
        const sign = temperature > 0 ? '+' : '';
        tempElement.textContent = sign + Math.round(temperature) + '°C';

        // Визуальные эффекты
        if (temperature > 50) {
            tempElement.style.color = 'var(--bar-bg)';
            tempElement.style.textShadow = '0 0 8px rgba(255, 0, 0, 0.5)';
        } else if (temperature > 39) {
            tempElement.style.color = 'var(--orange-light)';
            tempElement.style.textShadow = '0 0 8px rgba(255, 136, 0, 0.5)';
        } else {
            tempElement.style.color = 'var(--secondary-text)';
            tempElement.style.textShadow = '0 0 8px rgba(80, 255, 16, 0.5)';
        }

        engineWasRunning = engineRunning;
        return;  // Выходим, автопилот управляет всем сам
    }

    // ========== ОСНОВНАЯ ЛОГИКА (автопилот ВЫКЛЮЧЕН) ==========

    // Получаем текущую мощность с ползунка (если автопилот выключен)

    // ========== ДВИГАТЕЛЬ ВЫКЛЮЧЕН ==========
    if (!engineRunning) {
        // Логика остывания до 18°C, затем нагрев до 22°C через 3 секунды
        if (temperature > 22) {
            temperature -= 0.2;
            if (temperature < 22) temperature = 22;
            engineOffTimer = null;
            engineOffStartTime = null;
        } else if (temperature > 18 && temperature < 22) {
            if (engineOffStartTime === null) {
                engineOffStartTime = Date.now();
                engineOffTimer = setTimeout(() => {
                    engineOffTimer = null;
                }, 2000);
            }
            if (engineOffTimer === null && engineOffStartTime !== null) {
                temperature += 0.1;
                if (temperature > 22) temperature = 22;
            }
        } else if (temperature === 18) {
            if (engineOffStartTime === null) {
                engineOffStartTime = Date.now();
                engineOffTimer = setTimeout(() => {
                    engineOffTimer = null;
                }, 3000);
            }
            if (engineOffTimer === null && engineOffStartTime !== null) {
                temperature += 0.1;
                if (temperature > 22) temperature = 22;
            }
        }

        if (temperature > 22) temperature = 22;
        if (temperature < 18) temperature = 18;
    }

    // ========== ДВИГАТЕЛЬ РАБОТАЕТ ==========
    else {
        // Определяем направление движения ползунка
        if (currentPower > lastPower) {
            // Двинули ВПРАВО → мощность увеличилась
            if (heatingPhase === 0) heatingPhase = 3;
        } else if (currentPower < lastPower) {
            // Двинули ВЛЕВО → мощность уменьшилась
            if (heatingPhase === 0) heatingPhase = 3;
        }

        // ПРАВИЛО 5: Только что включили — разовый нагрев +0.5°C
        if (!engineWasRunning) {
            temperature += 0.5;
        }

        // ===== ОСНОВНАЯ ЛОГИКА =====
        if (heatingPhase > 0) {
            console.log('=== heatingPhase > 0 ===');
            console.log('lastPower:', lastPower);
            console.log('currentPower:', currentPower);
            console.log('lastPower < currentPower?', lastPower < currentPower);
            console.log('lastPower > currentPower?', lastPower > currentPower);
            console.log('  температура ДО:', temperature.toFixed(1));

            if (lastPower < currentPower) {
                console.log('→ ИДЕМ В НАГРЕВ');
                let newTemp = temperature + 2;
                if (currentPower >= 50 && currentPower <= 75) {
                    if (newTemp > 30) newTemp = 30;
                } else if (currentPower >= 10 && currentPower < 50) {
                    if (newTemp > 20) newTemp = 20;
                }
                temperature = newTemp;

            } else if (lastPower > currentPower) {
                console.log('→ ИДЕМ В ОСТЫВАНИЕ');
                let newTemp = temperature - 6;

                // Строгое ограничение по диапазонам мощности
                if (currentPower >= 80) {
                    if (newTemp < 50) newTemp = 50;
                } else if (currentPower > 75 && currentPower < 80) {
                    if (newTemp < 40) newTemp = 40;
                } else if (currentPower >= 50 && currentPower <= 75) {
                    if (newTemp < 30) newTemp = 30;
                } else if (currentPower >= 10 && currentPower < 50) {
                    if (newTemp < 20) newTemp = 20;
                } else if (currentPower < 10) {
                    if (newTemp < 18) newTemp = 18;
                }

                temperature = newTemp;
            } else {
                console.log('→ РАВНЫ, НИКУДА НЕ ИДЕМ');
            }

            lastPower = currentPower;
            console.log('  температура ПОСЛЕ:', temperature.toFixed(1));
            console.log('  -------------------');
            heatingPhase--;
        }


        // Если фаза быстрого изменения закончилась → стабилизация
        else {
            // СЛУЧАЙ 3: Ползунок стоит на месте или стабилизация после движения
            let targetTemp = 30;
            let steadySpeed = 0.6;

            if (currentPower >= 80) {
                targetTemp = 65;
            } else if (currentPower > 75 && currentPower < 80) {
                targetTemp = 40;
            } else if (currentPower >= 50 && currentPower <= 75) {
                targetTemp = 30;
            } else if (currentPower >= 10 && currentPower < 50) {
                targetTemp = 20;
            } else if (currentPower < 10) {
                targetTemp = 18;
            }

            // Плавно движемся к целевой температуре (с гистерезисом 2 градуса)
            if (temperature < targetTemp - 2) {
                temperature += steadySpeed;
                if (temperature > targetTemp) temperature = targetTemp;
            } else if (temperature > targetTemp + 2) {
                temperature -= steadySpeed;
                if (temperature < targetTemp) temperature = targetTemp;
            }
        }
    }
    // ПРАВИЛА 12-13: Гистерезис при переходе через 50% (только для точной мощности 50)
    if (previousPower >= 51 && currentPower === 50) {
        temperature -= 0.6;
    }
    if (previousPower <= 49 && currentPower === 50) {
        temperature += 0.6;
    }

    // Ограничения
    if (temperature > 65) temperature = 65;
    if (temperature < 18) temperature = 18;

    // Аварийные сообщения
    if (currentPower > 80 && temperature > 50) {
        const now = Date.now();
        if (now - lastWarningTime > 4000) {
            showMessage('КРИТИЧЕСКАЯ ТЕМПЕРАТУРА! СНИЗЬТЕ МОЩНОСТЬ!', 'error');
            lastWarningTime = now;
        }
    }

    if (currentPower > 80 && temperature === 65) {
        const now = Date.now();
        if (now - lastWarningTime > 4000) {
            showMessage('ПЕРЕГРЕВ! ОПАСНОСТЬ ПОВРЕЖДЕНИЯ ДВИГАТЕЛЯ!', 'error');
            lastWarningTime = now;
        }
    }

    if (temperature > 39 && temperature <= 50) {
        const now = Date.now();
        if (now - lastOrangeWarningTime > 4000) {
            showMessage('ПОВЫШЕННАЯ ТЕМПЕРАТУРА! СНИЗЬТЕ МОЩНОСТЬ!', 'warning');
            lastOrangeWarningTime = now;
        }
    }


    // ========== ОБНОВЛЕНИЕ ОТОБРАЖЕНИЯ ТЕМПЕРАТУРЫ ==========
    const sign = temperature > 0 ? '+' : '';
    tempElement.textContent = sign + Math.round(temperature) + '°C';

    // Меняем цвет в зависимости от температуры
    if (temperature > 50) {
        tempElement.style.color = 'var(--bar-bg)';
        tempElement.style.textShadow = '0 0 8px rgba(255, 0, 0, 0.5)';
    } else if (temperature > 39) {
        tempElement.style.color = 'var(--orange-light)';
        tempElement.style.textShadow = '0 0 8px rgba(255, 136, 0, 0.5)';
    } else {
        tempElement.style.color = 'var(--secondary-text)';
        tempElement.style.textShadow = '0 0 8px rgba(80, 255, 16, 0.5)';
    }
}


// ========== ОБРАБОТЧИК КНОПКИ ДВИГАТЕЛЯ ==========
engineBtn.addEventListener('click', () => {
    if (emergencyActive) {
        showMessage('АВАРИЙНЫЙ СТОП АКТИВЕН!', 'error');
        return;
    }

    engineRunning = !engineRunning;
    engineBtn.classList.toggle('active', engineRunning);

    if (engineRunning) {
        playEngineStartSound();
        showMessage('Двигатель запущен', 'success');
        statusLight.className = 'indicator-light green';

        // Вместо фиксированного интервала создаем функцию-цикл
        const runCycle = () => {
            if (!engineRunning) return; // Останавливаемся, если мотор выключен

            updateTemperature();
            // РАСЧЕТ СКОРОСТИ:
            // Если мощность > 80, цикл повторится через 200мс (5 раз в секунду)
            // Если мощность обычная, цикл повторится через 1000мс (1 раз в секунду)
            let nextTick = (currentPower > 80 || (heatingPhase > 0 && currentPower >= 50)) ? 200 : 1000;

            temperatureInterval = setTimeout(runCycle, nextTick);
        };

        runCycle(); // Запускаем первый раз
        updatePowerUI();
    } else {
        showMessage('Двигатель остановлен', 'warning');
        statusLight.className = 'indicator-light';
        clearInterval(temperatureInterval);
        temperature = 22;
        tempValue.textContent = '+22°C';
        // Сбрасываем таймеры выключенного двигателя
        engineOffTimer = null;
        engineOffStartTime = null;
        updatePowerUI();
    }
});

// ========== ОБРАБОТЧИК ШЛЮЗА ==========
airlockBtn.addEventListener('click', () => {
    if (emergencyActive) {

        showMessage('АВАРИЙНЫЙ СТОП АКТИВЕН!', 'error');
        return;
    }
    if (engineRunning) {
        showMessage('Нельзя открыть шлюз при работающем двигателе!', 'error');
        return;
    }
    airlockOpen = !airlockOpen;
    if (airlockOpen) {
        airlockBtn.style.background = 'radial-gradient(circle at 35% 35%, #66ff66, #00aa00)';
        airlockBtn.style.borderColor = '#008800';
        showMessage('Шлюз открыт', 'success');
    } else {
        airlockBtn.style.background = '';
        airlockBtn.style.borderColor = '';
        showMessage('Шлюз закрыт', 'warning');
    }
    playGatewaySound();
});

// ========== ОБРАБОТЧИК АВАРИЙНОГО СТОПА ==========
emergencyBtn.addEventListener('click', () => {
    emergencyActive = !emergencyActive;
    if (emergencyActive) {
        playEmergencyStopSound();
        engineRunning = false;
        engineBtn.classList.remove('active');
        clearInterval(temperatureInterval);
        temperature = 22;
        tempValue.textContent = '+22°C';
        tempValue.style.color = 'var(--secondary-text)';
        tempValue.style.textShadow = '0 0 8px rgba(80, 255, 16, 0.5)';
        statusLight.className = 'indicator-light red';
        emergencyBtn.style.animation = 'emergencyPulse 0.5s ease-in-out infinite';
        showMessage('АВАРИЙНЫЙ СТОП АКТИВИРОВАН!', 'error');
    } else {
        playSystemRestartSound();
        statusLight.className = 'indicator-light green';
        emergencyBtn.style.animation = '';
        showMessage('Система перезагружена', 'success');
    }
});

powerRange.addEventListener('input', (e) => {
    const val = parseInt(e.target.value);

    if (autoPilotActive) {
        powerRange.value = currentPower;  // Возвращаем ползунок назад
        showMessage('Автопилот активен! Сначала отключите автопилот.', 'error');
        return;
    }

    // Сохраняем старую мощность в lastPower ПЕРЕД изменением
    lastPower = currentPower;

    // Обновляем текущую мощность
    currentPower = val;

    // Синхронизируем ползунок и UI
    powerRange.value = val;
    engineFill.style.width = val + '%';
    engineValue.textContent = val;

    // Включаем фазу активного изменения
    heatingPhase = 3;
    updatePowerUI();

    // Звук только при реальном изменении
    if (val !== lastPowerValue) {
        playRandomSliderSound();
        lastPowerValue = val;
    }
});


const initialVal = powerRange.value;
currentPower = parseInt(initialVal);
engineFill.style.width = initialVal + '%';
engineValue.textContent = initialVal;
if (initialVal > 80) {
    engineFill.style.background = 'linear-gradient(90deg, #ff4444, #ff0000)';
    engineFill.style.boxShadow = '0 0 6px rgba(255, 0, 0, 0.3)';
} else {
    engineFill.style.background = 'var(--secondary-text)';
    engineFill.style.boxShadow = '0 0 6px rgba(80, 255, 16, 0.3)';
}

// ========== ОБРАБОТЧИК ЯРКОСТИ ==========
brightnessRange.addEventListener('input', (e) => {
    const val = e.target.value;
    brightnessFill.style.width = val + '%';
    brightnessValue.textContent = val;
    document.querySelector('.control-panel').style.filter = `brightness(${0.5 + val / 200})`;
});



// ========== ОБРАБОТЧИК АВТОПИЛОТА ==========
autoPilotToggle.addEventListener('change', (e) => {
    playAutoPilotToggleSound();
    autoPilotActive = e.target.checked;

    if (autoPilotActive) {
        // Включаем автопилот — устанавливаем мощность 75%
        setPower(75);
        showMessage('Автопилот активирован!', 'success');
        lowBatteryLight.style.animation = 'none';
        lowBatteryLight.style.opacity = '0.5';
    } else {
        showMessage('Ручное управление!', 'warning');
        lowBatteryLight.style.animation = '';
        lowBatteryLight.style.opacity = '';
    }
});

// ========== 3. СЛАЙДЕР ЯРКОСТИ ==========
if (brightnessRange && brightnessFill && brightnessValue) {
    brightnessRange.addEventListener('input', (e) => {
        const val = e.target.value;

        // Обновляем оранжевую полосу
        brightnessFill.style.width = val + '%';

        // АНИМАЦИЯ ЦИФРЫ - обновляем текстовое значение
        brightnessValue.textContent = val;

        // Меняем яркость панели (если элемент существует)
        const panel = document.querySelector('.control-panel');
        if (panel) {
            panel.style.filter = `brightness(${0.5 + val / 200})`;
        }
        // Звук только при реальном изменении
        if (val !== lastBrightnessValue) {
            playRandomSliderSound();
            lastBrightnessValue = val;
        }
    });

    // ========== ЗВУК ПРИ НАВЕДЕНИИ НА ФОТО ==========
    const photoTape = document.querySelector('.photo-tape');
    if (photoTape) {
        photoTape.addEventListener('mouseenter', () => {
            playPrayerSound(); // функция из sound.js
        });
    }

    // ========== ЗВУК SOS ПРИ НАВЕДЕНИИ НА ПАНЕЛЬ ЗАГОЛОВКА ==========
    const titlePlate = document.querySelector('.panel-title-plate');
    if (titlePlate) {
        titlePlate.addEventListener('mouseenter', () => {
            playSosSound();
        });
    }
    powerRange.addEventListener('mouseup', () => stopSliderSound());
    powerRange.addEventListener('mouseleave', () => stopSliderSound());

    brightnessRange.addEventListener('mouseup', () => stopSliderSound());
    brightnessRange.addEventListener('mouseleave', () => stopSliderSound());


}

// Initialize
updateTemperature();
