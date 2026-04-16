// INTERSECTION OBSERVER PARA ANIMAÇÕES DE SCROLL 3D (FADE-IN UP)
const observerOptions = {
    root: null,
    rootMargin: '0px',
    threshold: 0.15 // Dispara quando 15% do componente está visível
};

const observer = new IntersectionObserver((entries, observer) => {
    entries.forEach(entry => {
        if (entry.isIntersecting) {
            entry.target.classList.add('visible');
            observer.unobserve(entry.target); // Anima apenas 1 vez
        }
    });
}, observerOptions);

document.addEventListener('DOMContentLoaded', () => {
    const animatedElements = document.querySelectorAll('.animate-on-scroll');
    animatedElements.forEach(el => observer.observe(el));
});

// LÓGICA DO CARROSSEL DE IMAGENS 3D
const sliders = document.querySelectorAll('.slider');
const btnPrev = document.getElementById('prev-button');
const btnNext = document.getElementById('next-button');

let currentSlide = 0;

function hideSlider () {
    sliders.forEach(item => item.classList.remove('on'));
}

function showSlider () {
    if (sliders.length > 0) {
        sliders[currentSlide].classList.add('on');
    }
}

function nextSlider () {
    hideSlider();
    if(currentSlide === sliders.length - 1) {
        currentSlide = 0;
    } else {
        currentSlide++;
    }
    showSlider();
}

function prevSlider () {
    hideSlider();
    if(currentSlide === 0) {
        currentSlide = sliders.length - 1;
    } else {
        currentSlide--;
    }
    showSlider();
}

if(btnNext && btnPrev) {
    btnNext.addEventListener('click', nextSlider);
    btnPrev.addEventListener('click', prevSlider);
}

// LÓGICA DO FORMULÁRIO COM API FETCH MANTIDA
const form = document.getElementById("formContato");

if (form) {
    form.addEventListener("submit", async (e) => {
        e.preventDefault();

        // Botão visual feedback 
        const btnSubmit = form.querySelector('button[type="submit"]');
        const originalText = btnSubmit.innerText;
        btnSubmit.innerText = "Enviando...";
        btnSubmit.style.opacity = '0.7';

        const dados = {
            nome: form.nome.value,
            telefone: form.telefone.value,
            email: form.email.value,
            mensagem: form.mensagem.value
        };

        try {
            // Requisição original mantida intacta
            const response = await fetch("http://localhost:8080/api/leads", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(dados)
            });

            if (response.ok) {
                alert("Sua mensagem foi enviada com sucesso! Entrarei em contato em breve.");
                form.reset();
            } else {
                alert("Ops! Houve um erro ao enviar sua mensagem. Tente novamente.");
            }

        } catch (error) {
            alert("Erro de conexão com o servidor. A API local parece não estar rodando.");
        } finally {
            btnSubmit.innerText = originalText;
            btnSubmit.style.opacity = '1';
        }
    });
}

// MÁSCARA DO TELEFONE BRASILEIRO
const telefoneInput = document.getElementById("telefone");

if (telefoneInput) {
    telefoneInput.addEventListener("input", () => {
        let valor = telefoneInput.value.replace(/\D/g, "");

        if (valor.length > 11) {
            valor = valor.slice(0, 11);
        }

        // FORMATAÇÃO "(ZZ) YXXXX-XXXX"
        if (valor.length > 10) {
            valor = `(${valor.slice(0,2)}) ${valor.slice(2,7)}-${valor.slice(7,11)}`;
        } else if (valor.length > 6) {
            valor = `(${valor.slice(0,2)}) ${valor.slice(2,6)}-${valor.slice(6)}`;
        } else if (valor.length > 2) {
            valor = `(${valor.slice(0,2)}) ${valor.slice(2)}`;
        }

        telefoneInput.value = valor;
    });
}

// --- MENU MOBILE E DARK MODE LÓGICA ---
const mobileMenu = document.getElementById('mobile-menu');
const navLinks = document.getElementById('nav-links');
const themeSwitch = document.getElementById('theme-switch');

if (mobileMenu && navLinks) {
    mobileMenu.addEventListener('click', () => {
        navLinks.classList.toggle('active');
        const icon = mobileMenu.querySelector('i');
        if (navLinks.classList.contains('active')) {
            icon.classList.remove('bx-menu');
            icon.classList.add('bx-x');
        } else {
            icon.classList.remove('bx-x');
            icon.classList.add('bx-menu');
        }
    });

    // Fechar ao clicar nos links (útil no mobile)
    document.querySelectorAll('.nav-links a').forEach(link => {
        link.addEventListener('click', () => {
            navLinks.classList.remove('active');
            const icon = mobileMenu.querySelector('i');
            if (icon) {
                icon.classList.remove('bx-x');
                icon.classList.add('bx-menu');
            }
        });
    });
}

// LÓGICA THEME SWITCHER (MODO ESCURO)
if (themeSwitch) {
    const icon = themeSwitch.querySelector('i');
    
    // Checar modo salvo
    const savedMode = localStorage.getItem('darkMode') === 'true';
    if (savedMode) {
        document.body.classList.add('dark-mode');
        icon.classList.remove('bx-moon');
        icon.classList.add('bx-sun');
    }

    themeSwitch.addEventListener('click', () => {
        document.body.classList.toggle('dark-mode');
        const isDark = document.body.classList.contains('dark-mode');
        localStorage.setItem('darkMode', isDark);
        
        if (isDark) {
            icon.classList.remove('bx-moon');
            icon.classList.add('bx-sun');
            themeSwitch.title = "Ativar Modo Claro";
        } else {
            icon.classList.remove('bx-sun');
            icon.classList.add('bx-moon');
            themeSwitch.title = "Ativar Modo Escuro";
        }
    });
}
