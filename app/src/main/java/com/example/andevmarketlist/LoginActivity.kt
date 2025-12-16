package com.example.andevmarketlist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.andevmarketlist.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.Auth
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth= Firebase.auth

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val currentUser=auth.currentUser
        if(currentUser !=null){
            val intentUsuarioLogueado = Intent(this, pantalla_menu::class.java)
            startActivity(intentUsuarioLogueado)
        }

        binding.loginButton.setOnClickListener {
            val correo = binding.editEmail.text.toString()
            val password = binding.editPassword.text.toString()
            loginUsuario(correo, password)
        }

        binding.createUser.setOnClickListener {
            val correo = binding.editEmail.text.toString()
            val password = binding.editPassword.text.toString()
            crearUsuario(correo, password)
        }
    }

    fun loginUsuario(
        correo: String, password:String
    ){
        auth.signInWithEmailAndPassword(correo,password)
            .addOnCompleteListener{ task ->
                if (task.isSuccessful){
                    //login exitoso
                    val intentLogueado= Intent(this, pantalla_menu::class.java)
                    startActivity(intentLogueado)
                }else{
                    //login fallido
                    Toast.makeText(
                        baseContext,
                        "No pudo loguearse",
                        Toast.LENGTH_LONG,
                    ).show()
                }
            }
    }
    fun crearUsuario(correo: String, password: String) {

        if (correo.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Campos vacíos", Toast.LENGTH_SHORT).show()
            return
        }

        if (password.length < 6) {
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
            return
        }

        auth.createUserWithEmailAndPassword(correo, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Usuario creado correctamente", Toast.LENGTH_SHORT).show()

                } else {
                    val error = task.exception?.message
                    Toast.makeText(this, error, Toast.LENGTH_LONG).show()
                }
            }
    }
}