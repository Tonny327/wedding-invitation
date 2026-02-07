document.addEventListener('DOMContentLoaded', function() {
    // Initialize animations
    initScrollAnimations();
    initDecorativeElements();
    
    // RSVP Form handling
    const form = document.getElementById('rsvp-form');
    const formContainer = document.getElementById('rsvp-form-container');
    const successMessage = document.getElementById('success-message');
    const submitBtn = document.getElementById('submit-btn');
    const formError = document.getElementById('form-error');
    
    // Show hero immediately without animation
    const heroContent = document.querySelector('.hero .hero-content');
    if (heroContent) {
        heroContent.classList.add('show');
    }

    form.addEventListener('submit', async function(e) {
        e.preventDefault();
        
        // Clear previous errors
        clearErrors();
        formError.textContent = '';
        
        // Disable submit button
        submitBtn.disabled = true;
        submitBtn.textContent = 'Отправка...';

        // Get form data
        const formData = new FormData(form);
        const attendingValue = formData.get('attending');
        
        const guestData = {
            name: formData.get('name').trim(),
            attending: attendingValue === 'true',
            foodPreference: formData.get('foodPreference') || null,
            comment: formData.get('comment').trim() || null
        };

        try {
            const response = await fetch('/api/guests', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(guestData)
            });

            if (response.ok) {
                // Success - hide form and show success message
                formContainer.classList.add('hidden');
                successMessage.classList.remove('hidden');
                
                // Scroll to success message
                successMessage.scrollIntoView({ behavior: 'smooth', block: 'center' });
            } else {
                // Handle validation errors
                const errorData = await response.json();
                displayErrors(errorData);
                submitBtn.disabled = false;
                submitBtn.textContent = 'Отправить ответ';
            }
        } catch (error) {
            console.error('Error:', error);
            formError.textContent = 'Произошла ошибка при отправке. Пожалуйста, попробуйте еще раз.';
            submitBtn.disabled = false;
            submitBtn.textContent = 'Отправить ответ';
        }
    });

    function displayErrors(errors) {
        for (const [field, message] of Object.entries(errors)) {
            const errorElement = document.getElementById(field + '-error');
            if (errorElement) {
                errorElement.textContent = message;
            } else {
                formError.textContent = message;
            }
        }
    }

    function clearErrors() {
        const errorElements = document.querySelectorAll('.error-message');
        errorElements.forEach(el => el.textContent = '');
    }
    
    // IntersectionObserver for scroll animations
    function initScrollAnimations() {
        const observerOptions = {
            threshold: 0.1,
            rootMargin: '0px 0px -50px 0px'
        };
        
        const observer = new IntersectionObserver(function(entries) {
            entries.forEach((entry, index) => {
                if (entry.isIntersecting) {
                    // Add delay for staggered animations
                    setTimeout(() => {
                        entry.target.classList.add('show');
                    }, index * 50);
                    observer.unobserve(entry.target);
                }
            });
        }, observerOptions);
        
        // Observe all elements with .animate class (except hero content which is shown immediately)
        const animateElements = document.querySelectorAll('.animate');
        animateElements.forEach((el, index) => {
            // Skip hero content as it's shown immediately
            if (!el.classList.contains('hero-content')) {
                observer.observe(el);
            }
        });
    }
    
    // Create decorative floating elements
    function initDecorativeElements() {
        const sections = document.querySelectorAll('.hero, .photo-section, .invitation-text-section, .wedding-details-section');
        
        sections.forEach((section, sectionIndex) => {
            // Create 3-5 decorative elements per section
            const elementCount = Math.floor(Math.random() * 3) + 3;
            
            for (let i = 0; i < elementCount; i++) {
                const element = document.createElement('div');
                element.className = 'decorative-element';
                
                // Randomly choose between leaf and circle
                if (Math.random() > 0.5) {
                    element.classList.add('decorative-leaf');
                } else {
                    element.classList.add('decorative-circle');
                }
                
                // Random position
                const left = Math.random() * 100;
                const top = Math.random() * 100;
                const delay = Math.random() * 2;
                const duration = 6 + Math.random() * 4;
                
                element.style.left = left + '%';
                element.style.top = top + '%';
                element.style.animationDelay = delay + 's';
                element.style.animationDuration = duration + 's';
                
                section.appendChild(element);
            }
        });
    }
});

