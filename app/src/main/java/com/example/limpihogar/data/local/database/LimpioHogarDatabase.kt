package com.example.limpihogar.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.limpihogar.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [User::class, Category::class, Product::class, CartItem::class],
    version = 2, //  Subimos la versión
    exportSchema = false
)
abstract class LimpioHogarDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun categoryDao(): CategoryDao
    abstract fun productDao(): ProductDao
    abstract fun cartDao(): CartDao

    companion object {
        @Volatile
        private var INSTANCE: LimpioHogarDatabase? = null

        // Migración 1 → 2: agrega columna "role"
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE users ADD COLUMN role TEXT NOT NULL DEFAULT 'user'")
            }
        }

        fun getDatabase(context: Context): LimpioHogarDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LimpioHogarDatabase::class.java,
                    "limpiohogar_database"
                )
                    // Agregamos la migración segura
                    .addMigrations(MIGRATION_1_2)
                    // ⚙ Mientras desarrollas, puedes mantener fallback para evitar bloqueos si haces más cambios:
                    // .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback(context))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val context: Context
        ) : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateDatabase(database)
                    }
                }
            }
        }

        private suspend fun populateDatabase(database: LimpioHogarDatabase) {
            val categoryDao = database.categoryDao()
            val productDao = database.productDao()

            // Categorías
            val categories = listOf(
                Category(nombre = "Cocina", descripcion = "Limpieza de cocina y loza", icono = "🍳"),
                Category(nombre = "Baño", descripcion = "Limpieza y desinfección de baños", icono = "🚽"),
                Category(nombre = "Ropa", descripcion = "Detergentes y suavizantes", icono = "👕"),
                Category(nombre = "Pisos", descripcion = "Ceras y limpiadores de pisos", icono = "🧹"),
                Category(nombre = "Accesorios", descripcion = "Esponjas, paños y guantes", icono = "🧽"),
                Category(nombre = "Multiuso", descripcion = "Desinfectantes y limpiadores generales", icono = "✨")
            )
            categoryDao.insertCategories(categories)

            // Productos de muestra
            val products = listOf(
                Product(nombre = "Lavaloza Quix 1L", descripcion = "Desengrasante con aroma a limón", precio = 2990.0, stock = 50, categoriaId = 1, imagenUrl = "quix", marca = "Quix", calificacion = 4.7f, numeroReviews = 120, formato = "Botella 1L"),
                Product(nombre = "Cif Crema Limpiador", descripcion = "Limpiador cremoso multiuso", precio = 2490.0, precioAnterior = 2790.0, stock = 40, categoriaId = 1, imagenUrl = "cif", marca = "Cif", calificacion = 4.8f, numeroReviews = 98, formato = "Botella 750g"),
                Product(nombre = "Clorox Cloro Gel 900ml", descripcion = "Desinfecta, limpia y blanquea", precio = 3190.0, stock = 35, categoriaId = 2, imagenUrl = "clorox", marca = "Clorox", calificacion = 4.9f, numeroReviews = 210, formato = "Botella 900ml"),
                Product(nombre = "Pato Discos Activos", descripcion = "Gel limpiador adhesivo para inodoro", precio = 4990.0, stock = 30, categoriaId = 2, imagenUrl = "pato", marca = "Pato", calificacion = 4.5f, numeroReviews = 75, formato = "Pack 6 discos"),
                Product(nombre = "Detergente Ariel 3L", descripcion = "Concentrado para ropa blanca y de color", precio = 12990.0, precioAnterior = 14990.0, stock = 20, categoriaId = 3, imagenUrl = "ariel", marca = "Ariel", calificacion = 4.8f, numeroReviews = 340, formato = "Botella 3L"),
                Product(nombre = "Suavizante Downy 1.5L", descripcion = "Aroma fresco y duradero", precio = 6990.0, stock = 25, categoriaId = 3, imagenUrl = "downy", marca = "Downy", calificacion = 4.7f, numeroReviews = 180, formato = "Botella 1.5L"),
                Product(nombre = "Poett Limpiador Lavanda 1.8L", descripcion = "Aromatizante para pisos", precio = 3590.0, stock = 40, categoriaId = 4, imagenUrl = "poett", marca = "Poett", calificacion = 4.6f, numeroReviews = 112, formato = "Botella 1.8L"),
                Product(nombre = "Esponja Virutex (Pack 3)", descripcion = "Esponja multiuso", precio = 1990.0, stock = 100, categoriaId = 5, imagenUrl = "esponja", marca = "Virutex", calificacion = 4.4f, numeroReviews = 55, formato = "Pack 3 unidades"),
                Product(nombre = "Paños de Microfibra (Pack 5)", descripcion = "Paños reutilizables", precio = 4990.0, stock = 60, categoriaId = 5, imagenUrl = "panos", marca = "Genérica", calificacion = 4.5f, numeroReviews = 88, formato = "Pack 5 unidades"),
                Product(nombre = "Lysol Spray Desinfectante", descripcion = "Elimina el 99.9% de gérmenes", precio = 5990.0, stock = 30, categoriaId = 6, imagenUrl = "lysol", marca = "Lysol", calificacion = 4.9f, numeroReviews = 410, formato = "Aerosol 340g")
            )
            productDao.insertProducts(products)
        }
    }
}
