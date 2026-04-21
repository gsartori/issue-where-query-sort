package dueunoapp

import grails.compiler.GrailsCompileStatic
import grails.gorm.MultiTenant
import org.grails.datastore.gorm.GormEntity

import java.time.LocalDate
import java.time.LocalDateTime

@GrailsCompileStatic
class TWorkPackage implements GormEntity, MultiTenant<TWorkPackage> {

    Long id
    LocalDateTime dateCreated
    String usernameCreated

    String code
    String description
    LocalDate validFrom
    LocalDate validTo

    Set<TWorkAssignment> workAssignments
    static hasMany = [
            workAssignments: TWorkAssignment,
    ]

    static constraints = {
        code unique: true
        description nullable: true

        validFrom validator: { LocalDate val, TWorkPackage obj ->
            if (val && obj.validTo && val.isAfter(obj.validTo)) {
                return 'invalid.period'
            }
        }
        validTo validator: { LocalDate val, TWorkPackage obj ->
            if (val && obj.validFrom && val.isBefore(obj.validFrom)) {
                return 'invalid.period'
            }
        }
    }

}
