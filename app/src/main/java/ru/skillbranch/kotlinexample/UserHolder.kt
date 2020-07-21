package ru.skillbranch.kotlinexample

import ru.skillbranch.kotlinexample.User.Factory.trimLogin

object UserHolder {
    private val map = mutableMapOf<String, User>()

    private fun addUniqUser(user: User, error: String) {
        if (map.containsKey(user.login))
            throw IllegalArgumentException(error)
        map[user.login] = user
    }
//  Реализуй метод registerUser(fullName: String, email: String, password: String) возвращающий объект User,
//  если пользователь с таким же логином уже есть в системе необходимо бросить исключение
//  IllegalArgumentException("A user with this email already exists")
    fun registerUser(
        fullName: String,
        email: String,
        password: String
    ) : User {
    println(">> registerUser(fullName = $fullName, email = $email, password = $password)")
        return User.makeUser(fullName, email = email, password = password)
            .also { addUniqUser(it, "A user with this email already exists") }
    }

//  Реализуй метод registerUserByPhone(fullName: String, rawPhone: String) возвращающий объект User
//  (объект User должен содержать поле accessCode с 6 значным значением состоящим из случайных строчных
//  и прописных букв латинского алфавита и цифр от 0 до 9), если пользователь с таким же телефоном уже
//  есть в системе необходимо бросить ошибку IllegalArgumentException("A user with this phone already exists")
//  валидным является любой номер телефона содержащий первым символом + и 11 цифр и не содержащий буквы,
//  иначе необходимо бросить исключение IllegalArgumentException("Enter a valid phone number starting with a + and containing 11 digits")

    fun registerUserByPhone(fullName : String, rawPhone: String): User {
        println(">> registerUserByPhone(fullName = $fullName, rawPhone = $rawPhone")
        if (!User.isPhoneValid(rawPhone))
            throw IllegalArgumentException("Enter a valid phone number starting with a + and containing 11 digits")
        return User.makeUser(fullName, phone = rawPhone)
            .also { addUniqUser(it, "A user with this phone already exists") }
    }

    fun loginUser(login: String, password: String): String? {
        println(">> loginUser(login = $login, password = $password")
        return map[login.trimLogin()]?.run {
            if (checkPassword(password)) this.userInfo
            else null
        }
    }

    fun importUsers(csv: List<String>): List<User> {
        println(">> importUsers")
        return csv.mapNotNull { lineStr ->
            println("l: '$lineStr'")
            lineStr.trim().split(";").takeIf { it.size >= 3 }?.let { line ->
                val (salt, pass) = line[2].split(":").let { it[0] to it[1] }
                val fullName = line[0]
                val email = line[1].takeIf { it.isNotEmpty() }
                val rawPhone = line[3].takeIf { it.isNotEmpty() }

                User.importUser(
                    fullName = fullName,
                    email = email,
                    rawPhone = rawPhone,
                    salt = salt,
                    passwordHash = pass
                ).also {
                    map[it.login] = it
//                    addUniqUser(it, "A user with this login already exists")
                }
            }
        }
    }


    fun clearHolder() {
        map.clear()
    }

//  Реализуй метод requestAccessCode(login: String) : Unit, после выполнения данного метода
//  у пользователя с соответствующим логином должен быть сгенерирован новый код авторизации
//  и помещен в свойство accessCode, соответственно должен измениться и хеш пароля пользователя
//  (вызов метода loginUser должен отрабатывать корректно)
    fun requestAccessCode(rawPhone: String) {
    println(">> requestAccessCode(rawPhone = $rawPhone")
    val phone = rawPhone.trimLogin()
    map[phone]?.resetAccessCode()
}

}