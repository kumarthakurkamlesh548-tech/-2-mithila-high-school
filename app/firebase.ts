import { initializeApp } from "firebase/app";
import { getAuth } from "firebase/auth";
import { getFirestore } from "firebase/firestore";
import { getStorage } from "firebase/storage";

const firebaseConfig = {
  apiKey: "AIzaSyCPO4NSPfTYpyTO6mZj4HavFwB2BHVYYi0",
  authDomain: "students-71ec1.firebaseapp.com",
  projectId: "students-71ec1",
  storageBucket: "students-71ec1.firebasestorage.app",
  messagingSenderId: "744131473562",
  appId: "1:744131473562:web:44798622561dedb7dfef23"
};

const app = initializeApp(firebaseConfig);

export const auth = getAuth(app);
export const db = getFirestore(app);
export const storage = getStorage(app);
