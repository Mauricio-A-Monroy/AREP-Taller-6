# Secure Application Design - Enterprise Architecture Workshop

## Resumen del Proyecto

Este proyecto consiste en el diseño y despliegue de una aplicación segura y escalable en AWS. La arquitectura incluye dos servidores principales:

1. **Servidor Apache:** Encargado de servir el cliente asíncrono (HTML + JavaScript) a través de una conexión segura con TLS.
2. **Servidor Spring Boot:** Responsable de los servicios backend, exponiendo endpoints REST protegidos con TLS.

## Arquitectura del Sistema

El sistema consta de los siguientes componentes:

- **Frontend:** Aplicación web en HTML, CSS y JavaScript, comunicándose con el backend mediante fetch.
- **Backend:** API REST en Java con Spring Boot.
- **Base de Datos:** Se puede utilizar MySQL o PostgreSQL para almacenar usuarios y datos de la aplicación.
- **Seguridad:** Implementación de TLS con certificados de Let's Encrypt y autenticación segura con contraseñas hasheadas.
- **Despliegue en AWS:** Los servicios están desplegados en instancias EC2 separadas para Apache y Spring Boot.

## Diagrama de Arquitectura

![diagrama despliegue](https://github.com/user-attachments/assets/22b12537-635d-45cf-9ea1-58e1140001b2)


## Seguridad Implementada

1. **TLS Encryption:** Certificados TLS de Let's Encrypt para Apache y Spring Boot.
2. **Autenticación Segura:** Hashing de contraseñas antes de almacenarlas en la base de datos.
3. **Configuración de Firewall:** Reglas en AWS para permitir solo conexiones seguras.

## Configuración del Servidor Apache

### 1. Instalar y configurar Apache en EC2
```sh
sudo yum update -y
sudo yum install httpd -y
sudo systemctl start httpd
sudo systemctl enable httpd
sudo systemctl status httpd
```

### 2. Configurar VirtualHost para el Backend
```sh
sudo nano /etc/httpd/conf.d/spring-framework-server.conf
```
Añadir lo siguiente:
```apache
<VirtualHost *:80>
    ServerName spring-framework-server.duckdns.org
    DocumentRoot /var/www/html

    <Directory /var/www/html>
        AllowOverride All
        Require all granted
    </Directory>

    ErrorLog /var/log/httpd/spring-framework-server_error.log
    CustomLog /var/log/httpd/spring-framework-server_access.log combined
</VirtualHost>
```

### 3. Reiniciar Apache
```sh
sudo systemctl restart httpd
```

---

## Configurar HTTPS con Certbot

### 1. Instalar Certbot y habilitar SSL en Apache
```sh
sudo yum install -y epel-release
sudo yum install -y certbot python-certbot-apache
```

### 2. Generar certificado SSL para el backend
```sh
sudo certbot --apache -d spring-framework-server.duckdns.org
```

### 3. Configurar VirtualHost para HTTPS
```sh
sudo nano /etc/httpd/conf.d/spring-framework-server-le-ssl.conf
```
Añadir lo siguiente:
```apache
<IfModule mod_ssl.c>
<VirtualHost *:443>
    ServerName spring-framework-server.duckdns.org
    DocumentRoot /var/www/html

    <Directory /var/www/html>
        AllowOverride All
        Require all granted
    </Directory>

    ErrorLog /var/log/httpd/spring-framework-server_error.log
    CustomLog /var/log/httpd/spring-framework-server_access.log combined

    ProxyPass "/api/" "http://localhost:8080/"
    ProxyPassReverse "/api/" "http://localhost:8080/"

    SSLCertificateFile /etc/letsencrypt/live/spring-framework-server.duckdns.org/fullchain.pem
    SSLCertificateKeyFile /etc/letsencrypt/live/spring-framework-server.duckdns.org/privkey.pem
    Include /etc/letsencrypt/options-ssl-apache.conf
</VirtualHost>
</IfModule>
```

---

## Desplegar Backend (Spring Boot)

### 1. Configurar Maven para empaquetar la aplicación
En el `pom.xml`, asegúrate de incluir:
```xml
<plugin>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-maven-plugin</artifactId>
    <executions>
        <execution>
            <goals>
                <goal>repackage</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

### 2. Compilar y subir el JAR a EC2
```sh
mvn clean package
scp -i "KeyProperty.pem" "target/AREP-Taller-6-1.0-SNAPSHOT.jar" ec2-user@54.210.2.108:/home/ec2-user/
```

### 3. Instalar Java en EC2 y ejecutar el backend
```sh
sudo yum install -y java-21-amazon-corretto-devel
nohup java -jar AREP-Taller-6-1.0-SNAPSHOT.jar --server.port=8080 > log.out 2>&1 &
tail -f log.out
```

---

## Desplegar el Frontend en Apache

### 1. Copiar los archivos estáticos a EC2
```sh
scp -i keyApache.pem -r /c/Users/Mauricio\ Monroy/AREP/AREP-Taller-6/src/main/resources/static/* ec2-user@52.201.223.24:/var/www/html/
```

### 2. Configurar VirtualHost para el Frontend
```sh
sudo nano /etc/httpd/conf.d/frontend.conf
```
Añadir lo siguiente:
```apache
<VirtualHost *:80>
    ServerName frontend-apache-server.duckdns.org
    DocumentRoot "/var/www/html"

    ErrorLog "/var/log/httpd/frontend-error.log"
    CustomLog "/var/log/httpd/frontend-access.log" combined

    RewriteEngine on
    RewriteCond %{SERVER_NAME} =frontend-apache-server.duckdns.org
    RewriteRule ^ https://%{SERVER_NAME}%{REQUEST_URI} [END,NE,R=permanent]
</VirtualHost>

<VirtualHost *:443>
    ServerName frontend-apache-server.duckdns.org
    DocumentRoot /var/www/html

    <Directory /var/www/html>
        Options Indexes FollowSymLinks
        AllowOverride All
        Require all granted
    </Directory>

    SSLEngine on
    SSLCertificateFile /etc/letsencrypt/live/frontend-apache-server.duckdns.org/fullchain.pem
    SSLCertificateKeyFile /etc/letsencrypt/live/frontend-apache-server.duckdns.org/privkey.pem
</VirtualHost>
```

### 3. Generar certificado SSL para el Frontend
```sh
sudo yum install -y certbot python3-certbot-apache
sudo certbot --apache -d frontend-apache-server.duckdns.org
```

---

## Mantenimiento y Debugging

### Ver logs de Apache
```sh
tail -f /var/log/httpd/error_log
```

### Verificar estado de Apache y Java
```sh
sudo systemctl status httpd
ps aux | grep java
```

### Detener procesos en el puerto 8080
```sh
sudo lsof -i :8080
sudo kill -9 <PID>
```

---

## Pruebas

![image](https://github.com/user-attachments/assets/7b98aef3-cea9-4e94-9445-aca26a64b51d)


## Video de Despliegue

[Ver Video](https://www.youtube.com/watch?v=cCsB76NrJMI)

## Repositorio

Todo el código está disponible en [GitHub](https://github.com/tu-repositorio).



## Conclusión

Con estos pasos, tienes un **backend en Spring Boot** corriendo en **EC2**, con un **frontend estático servido por Apache** y un **certificado SSL configurado con Certbot**. 

