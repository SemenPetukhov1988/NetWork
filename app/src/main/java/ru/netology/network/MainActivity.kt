package ru.netology.network
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import dagger.hilt.android.AndroidEntryPoint
import jakarta.inject.Inject
import kotlinx.coroutines.launch
import ru.netology.network.repository.LocalAuthRepository


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject lateinit var localAuthRepository: LocalAuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main) // Твой layout с FragmentContainerView

        // Edge-to-edge (для Material You). Можно убрать, если у тебя другая тема.
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // НАЙДИ ФРАГМЕНТ-ХОСТ И ПОЛУЧИ КОНТРОЛЛЕР ЭТИМ СПОСОБОМ
        val navHost = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController: NavHostController = navHost.navController as NavHostController

//        setupActionBarWithNavController(navController)

        lifecycleScope.launch { // Проверяем токен асинхронно при старте
            if (localAuthRepository.isLoggedIn()) {
                // Если есть токен — переходим на ленту через ГЛОБАЛЬНОЕ действие
                navController.navigate(R.id.action_global_to_wall)
            }

            // Если НЕ залогинен — ничего не делаем.
            // Навигатор сам покажет стартовый экран авторизации,
            // так как app:startDestination="@id/authFragment" в nav_graph.xml
        }
    }

    // ОБЯЗАТЕЛЬНЫЙ МЕТОД ДЛЯ КНОПКИ "НАЗАД"
    // Оставляем его без изменений из твоего варианта
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}