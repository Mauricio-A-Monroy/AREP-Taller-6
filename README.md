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
- scp -i "KeyProperty.pem" "C:\Users\Mauricio Monroy\AREP\AREP-Taller-6\target\AREP-Taller-6-1.0-SNAPSHOT.jar" ec2-user@54.243.25.146:/home/ec2-user/


sudo yum install -y java-21-amazon-corretto-devel

