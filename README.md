## DEspliegue back en EC2:

- sudo yum install cronie -y
-  sudo systemctl enable crond
sudo systemctl start crond
- sudo systemctl status crond
-  crontab -l
- 

- sudo yum update -y
- sudo yum install httpd -y
- sudo systemctl start httpd
sudo systemctl enable httpd
- sudo systemctl status httpd

- sudo yum install -y epel-release
sudo yum install -y certbot python-certbot-apache


sudo nano /etc/httpd/conf.d/spring-framework-server.conf
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
sudo systemctl restart httpd
sudo certbot --apache -d spring-framework-server.duckdns.org

pasar jar a EC2
<plugin>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-maven-plugin</artifactId>
        <version>3.2.2</version>
        <executions>
          <execution>
            <goals>
              <goal>repackage</goal>
            </goals>
          </execution>
        </executions>
      </plugin>

      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-jar-plugin</artifactId>
        <version>3.3.0</version>
        <configuration>
          <archive>
            <manifest>
              <mainClass>edu.escuelaing.arep.App.Secureweb</mainClass>
            </manifest>
          </archive>
        </configuration>
      </plugin>
      
- scp -i "KeyProperty.pem" "C:\Users\Mauricio Monroy\AREP\AREP-Taller-6\target\AREP-Taller-6-1.0-SNAPSHOT.jar" ec2-user@54.243.25.146:/home/ec2-user/


sudo yum install -y java-21-amazon-corretto-devel
- nohup java -jar AREP-Taller-6-1.0-SNAPSHOT.jar --server.port=8080 > log.out 2>&1 &
- tail -f log.out


sudo nano /etc/httpd/conf.d/spring-framework-server-le-ssl.conf
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






