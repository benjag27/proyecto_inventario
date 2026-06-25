Flujo para traer cambios luego de haber modificado.
git add .
git commit -m "Avance en login"

git checkout main
git pull origin main

git checkout juani

git merge main

git push origin juani


Para correr proyecto:
mvn clean compile
mvn javafx:run

comando para levantar server mysql
sudo systemctl start mysql


Schema de productos 
CREATE TABLE IF NOT EXISTS productos (
    id INT AUTO_INCREMENT,
    name VARCHAR(150) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


schema de users
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL
);

admin
admin123 = $2a$10$tzBis5Jc8UZ2ZoMENK3B9O6sCNHqBiogJRizR34UC5752r4jkphNe
