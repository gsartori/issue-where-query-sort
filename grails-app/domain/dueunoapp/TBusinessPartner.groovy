package dueunoapp

import grails.compiler.GrailsCompileStatic
import grails.gorm.MultiTenant
import org.grails.datastore.gorm.GormEntity

import java.time.LocalDateTime

@GrailsCompileStatic
class TBusinessPartner implements GormEntity, MultiTenant<TBusinessPartner> {

    Long id
    LocalDateTime dateCreated
    String usernameCreated

    // Person
    String firstname
    String lastname

}
