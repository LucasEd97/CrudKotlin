package com.example.bankaccount

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("accounts")
class AccountController(val repository: AccountRepository) {

    @PostMapping
    //Por causa da notation @RequestBody ele consegue fazer o parse do JSON para a classe de modelo
    fun create(@RequestBody account: Account) = ResponseEntity.ok(repository.save(account))

    @GetMapping
    fun read() = ResponseEntity.ok(repository.findAll())

    @PutMapping("{document}")
    fun update(@PathVariable document: String, @RequestBody account: Account): ResponseEntity<Account> {
        val accountDBOptional = repository.findByDocument(document)
        //accountDBOptional.orElseThrow  = Caso não exista é retornada a Exception,
        // caso exista a account é feito um Get no accountDBOptional e repassado o valor para accountDB
        val accountDB = accountDBOptional.orElseThrow { RuntimeException("Account document: $document not found!") }
        //Alimenta o saved com os dados enviados no body da requisição
        val saved = repository.save(accountDB.copy(name = account.name, balance = account.balance))
        return ResponseEntity.ok(saved)
    }

    @DeleteMapping("{document}")
    fun delete(@PathVariable document: String) = repository
        //Busca pelo documento no banco
        .findByDocument(document)
        //Caso encontre, o mesmo é deletado. It=Parametro encontrado pelo Find,  nesse caso a Account encontrada
        .ifPresent { repository.delete(it) }

}