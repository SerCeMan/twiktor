Twiktor
=======
[![SA status](http://ninebugs.cloudapp.net/result/SerCeMan/twiktor)](http://ninebugs.cloudapp.net/repo/SerCeMan/twiktor)

Twiktor - проект на хакатон в СПбАУ. 

Идея:  
Создать платформу управленя ботами, где бот - искусственный интеллект в 140 символов - пользователь твиттера. Боты живут своей жизнью, набирают аудиторию подписчиков и в некоторые моменты массово начинают постить твиты на заданную тематику.

Features
---------------
* Пользователь может добавить произвольное количество ботов
* Боты добавляются через поддержку протокола OAuth
* Пользователь может добавить произвольное количетсво тем, на которые будут общаться боты
* Пользователь может добавлять самые популярыные темы в автоматическом режиме
* Пользователь может в любой момент приостановить или запустить бота
* Пользователь может отправить сообщение сразу во все ленты ботов, причем данное сообщение будет уникальным у большинства ботов
* Боты отвечают на чужие твиты оригинальным контентом, преобразованным алгоритмически из контента других пользователей, за счет чего создается иллюзия ответа живого пользователя

Build
---------------
```bash
mvn clean package && java -jar target/twiktor-1.0.jar
```
