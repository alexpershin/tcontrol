const form = document.getElementById('loginForm');
const usernameInput = document.getElementById('username');
const passwordInput = document.getElementById('password');
const errorMsg = document.getElementById('errorMsg');

form.addEventListener('submit', function(e) {
  e.preventDefault();

  const user = usernameInput.value.trim();
  const pass = passwordInput.value.trim();

 /* if (user === 'admin' && pass === 'password123') {
    errorMsg.textContent = '';
    alert('Login successful!');
    // Redirect or update state here
  } else {
    errorMsg.textContent = 'Invalid username or password.';
  }*/
  handleLogin(e, user, pass)
});

async function handleLogin(e, user, pass) {
  e.preventDefault();

  // 1. Perform your authentication API call
  const response = await fetch(window.location.protocol
                                       + '//' + window.location.host
                                       + "/tcontrol/auth/sign-in",
                                       { method: "POST",
                                         headers: {
                                               "Content-Type": "application/json", // Tells the server you are sending JSON
                                               "Accept": "application/json"        // Tells the server you expect JSON back
                                             },
                                       body: JSON.stringify({login: user, password: pass})
                               });
  const data = await response.json();

  if (data.token) {
    // 2. Save the token
    localStorage.setItem('auth_token', data.token);

    // 3. Read the 'redirectTo' parameter from URL
    const urlParams = new URLSearchParams(window.location.search);
    const redirectTo = urlParams.get("redirectTo");

    // 4. Redirect back to the original page or fallback to the home dashboard
    window.location.href = redirectTo ? decodeURIComponent(redirectTo) : "/tcontrol/sensors.html";
  }
}