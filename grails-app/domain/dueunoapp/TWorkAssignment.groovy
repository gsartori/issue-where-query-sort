package dueunoapp

import grails.compiler.GrailsCompileStatic
import grails.gorm.MultiTenant
import org.grails.datastore.gorm.GormEntity

import java.time.LocalDate
import java.time.LocalDateTime

@GrailsCompileStatic
class TWorkAssignment implements GormEntity, MultiTenant<TWorkAssignment> {

    Long id
    LocalDateTime dateCreated
    String usernameCreated

    TBusinessPartner individual
    LocalDate validFrom
    LocalDate validTo

    TWorkPackage workPackage
    static belongsTo = [
            workPackage: TWorkPackage,
    ]

    static constraints = {
        validTo validator: { LocalDate val, TWorkAssignment obj ->
            if (val && obj.validFrom && val.isBefore(obj.validFrom)) {
                return 'invalid.period'
            }
        }
    }

}
