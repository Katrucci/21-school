let currentPrayerAudio = null;
let currentDangerAudio = null;
let currentSosAudio = null;
let lastSliderSoundTime = 0;
let globalVolume = 1;
let currentSliderAudio = null;


function createAudioWithVolume(src, volume = globalVolume) {
    const audio = new Audio(src);
    audio.volume = volume;
    return audio;
}


// ========== МОЛИТВА ==========
function playPrayerSound() {
    // Если звук уже играет — останавливаем его и сбрасываем
    if (currentPrayerAudio && !currentPrayerAudio.paused) {
        currentPrayerAudio.pause();
        currentPrayerAudio.currentTime = 0;
        currentPrayerAudio = null;
        return; // повторное наведение останавливает и не запускает заново
    }

    // Если звук не играет (или был остановлен) — создаём новый
    const audio = new Audio('sound/prayer_jp.mp3');
    currentPrayerAudio = audio;
    audio.play().catch(e => console.log('Ошибка воспроизведения:', e));

    // Когда звук закончится сам, очищаем переменную
    audio.addEventListener('ended', () => {
        if (currentPrayerAudio === audio) currentPrayerAudio = null;
    });
}

// ========== ДВИГАТЕЛЬ ==========
function playEngineStartSound() {
    const audio = new Audio('sound/engine.wav'); // путь к файлу
    audio.play().catch(e => console.log('Ошибка воспроизведения звука двигателя:', e));
}

// ========== СТОП ==========
function playEmergencyStopSound() {
    const audio = new Audio('sound/stop.mp3');
    audio.play().catch(e => console.log('Ошибка воспроизведения звука аварийного стопа:', e));
}
function playSystemRestartSound() {
    const audio = new Audio('sound/stop.mp3');
    audio.play().catch(e => console.log('Ошибка воспроизведения звука перезагрузки:', e));
}

// ========== АВТОПИЛОТ ==========
function playAutoPilotToggleSound() {
    const audio = createAudioWithVolume('sound/autoPilot.mp3', 0.05);
    audio.play().catch(e => console.log('Ошибка воспроизведения звука автопилота:', e));
}


// ========== ERROR ==========
function playDangerSound() {
    // Если уже играет какой-то звук опасности — останавливаем его
    if (currentDangerAudio && !currentDangerAudio.paused) {
        currentDangerAudio.pause();
        currentDangerAudio.currentTime = 0;
    }
    const audio = new Audio('sound/danger.mp3');
    currentDangerAudio = audio;
    audio.play().catch(e => console.log('Ошибка воспроизведения звука опасности:', e));
    // Когда звук закончится сам — очищаем переменную
    audio.addEventListener('ended', () => {
        if (currentDangerAudio === audio) currentDangerAudio = null;
    });
}

function stopDangerSound() {
    if (currentDangerAudio && !currentDangerAudio.paused) {
        currentDangerAudio.pause();
        currentDangerAudio.currentTime = 0;
        currentDangerAudio = null;
    }
}


// ========== ШЛЮЗ ==========
function playGatewaySound() {
    const audio = new Audio('sound/get2.mp3');
    audio.play().catch(error => {
        // Молча игнорируем ошибку автозапуска или логируем в консоль
        if (error.name !== 'NotAllowedError') {
            console.warn('Audio play failed:', error);
        }
    });
}


// ========== SOS АЗБУКА МОРЗЕ ==========
function playSosSound() {
    // Если звук уже играет — останавливаем его и сбрасывае
    if (currentSosAudio && !currentSosAudio.paused) {
        currentSosAudio.pause();
        currentSosAudio.currentTime = 0;
        currentSosAudio = null;
        return
    }

    const audio = new Audio('sound/SOS.mp3');
    currentSosAudio = audio;
    audio.play().catch(e => console.log('Ошибка воспроизведения SOS:', e));
    audio.addEventListener('ended', () => {
        if (currentSosAudio === audio) currentSosAudio = null;
    });
}

// ========== ПОЛЗУНКИ ==========
const sliderSounds = [
    'sound/1.mp3',
    'sound/2.mp3',
    'sound/3.mp3'
];

// Функция для воспроизведения случайного звука при движении ползунка
function playRandomSliderSound() {
    const now = Date.now();
    if (now - lastSliderSoundTime < 150) return;
    lastSliderSoundTime = now;

    // Останавливаем предыдущий звук, если он ещё играет
    if (currentSliderAudio && !currentSliderAudio.paused) {
        currentSliderAudio.pause();
        currentSliderAudio.currentTime = 0;
    }

    const randomIndex = Math.floor(Math.random() * sliderSounds.length);
    const audio = createAudioWithVolume(sliderSounds[randomIndex], 0.1);
    currentSliderAudio = audio;
    audio.play().catch(e => console.log('Ошибка воспроизведения звука ползунка:', e));

    // Когда звук закончится сам — очищаем переменную
    audio.addEventListener('ended', () => {
        if (currentSliderAudio === audio) currentSliderAudio = null;
    });
}

function stopSliderSound() {
    if (currentSliderAudio && !currentSliderAudio.paused) {
        currentSliderAudio.pause();
        currentSliderAudio.currentTime = 0;
        currentSliderAudio = null;
    }
}
