// Import the functions you need from the SDKs you need
import { initializeApp } from "firebase/app";
import { getAuth, GoogleAuthProvider } from "firebase/auth";
// TODO: Add SDKs for Firebase products that you want to use
// https://firebase.google.com/docs/web/setup#available-libraries

// Your web app's Firebase configuration
const firebaseConfig = {
  apiKey: "AIzaSyCt3O572UFNWqS69y5yJJDshQoF0GXDmVw",
  authDomain: "collabsphere-auth.firebaseapp.com",
  projectId: "collabsphere-auth",
  storageBucket: "collabsphere-auth.firebasestorage.app",
  messagingSenderId: "187716265624",
  appId: "1:187716265624:web:fc60097ec5fa9eddbed2ed"
};

// Initialize Firebase
const app = initializeApp(firebaseConfig);
export const auth = getAuth(app);
const provider = new GoogleAuthProvider();

// Dòng này ép Google luôn hiện bảng "Chọn tài khoản"
provider.setCustomParameters({
  prompt: 'select_account'
});
export const googleProvider = new GoogleAuthProvider();