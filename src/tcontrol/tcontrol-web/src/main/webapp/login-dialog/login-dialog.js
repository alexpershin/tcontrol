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

function arrayBufferToBase64Sync(buffer) {
  let binary = '';
  const bytes = new Uint8Array(buffer);
  const len = bytes.byteLength;

  for (let i = 0; i < len; i++) {
    binary += String.fromCharCode(bytes[i]);
  }

  return window.btoa(binary);
}

async function handleLogin(e, user, pass) {
  e.preventDefault();

  const preSignInResponse = await fetch(window.location.protocol
                                         + '//' + window.location.host
                                         + "/tcontrol/auth/pre-sign-in?login="+user,
                                         { method: "POST"});

  const publicKeyBytes = await preSignInResponse.bytes();
  const keyBuffer = new Uint8Array(publicKeyBytes);

  console.log("public key:")
  console.log(keyBuffer)

  const publicKey = await window.crypto.subtle.importKey(
      "spki",
      keyBuffer,
      {
        name: "RSA-OAEP",
        hash: { name: "SHA-256" } // Secure hashing choice
      },
      false, // The key cannot be extracted back out
      ["encrypt"]
  );

  const encodedPass = new TextEncoder().encode(pass);

  const encryptedBuffer = await window.crypto.subtle.encrypt(
       { name: "RSA-OAEP" },
       publicKey,
       encodedPass
  );

  base64Password = arrayBufferToBase64Sync(encryptedBuffer)

  // 1. Perform your authentication API call
  const response = await fetch(window.location.protocol
                                       + '//' + window.location.host
                                       + "/tcontrol/auth/sign-in",
                                       { method: "POST",
                                         headers: {
                                               "Content-Type": "application/json", // Tells the server you are sending JSON
                                               "Accept": "application/json"        // Tells the server you expect JSON back
                                             },
                                       body: JSON.stringify({login: user, password: base64Password})
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