package dueunoapp

import dueuno.core.ApplicationService
import dueuno.properties.TenantPropertyService
import dueuno.security.SecurityService
import dueuno.types.Money
import dueuno.types.Quantity
import dueuno.types.QuantityService
import dueuno.types.QuantityUnit
import grails.web.servlet.mvc.GrailsHttpSession
import template.*

import java.time.LocalDate

class BootStrap {

    TenantPropertyService tenantPropertyService
    ApplicationService applicationService
    SecurityService securityService
    QuantityService quantityService

    BusinessPartnerService businessPartnerService
    WorkPackageService workPackageService
    WorkAssignmentService workAssignmentService

    def init = {

        applicationService.onInstall {
            // no-op
        }

        applicationService.onTenantInstall { String tenantId ->
            tenantPropertyService.setString('PRIMARY_BACKGROUND_COLOR', '#cc0000')
            tenantPropertyService.setString('LOGIN_COPY', '<a href="https://dueuno.com" target="_blank">Dueuno</a> &copy; ' + LocalDate.now().year)
            quantityService.enableUnit(QuantityUnit.PCS)

            securityService.updateGroup(tenantId: tenantId, name: 'USERS', landingPage: 'workAssignment')

            def james = businessPartnerService.create(failOnError: true, usernameCreated: 'super', firstname: 'James', lastname: 'Fredley')
            def gianluca = businessPartnerService.create(failOnError: true, usernameCreated: 'super', firstname: 'Gianluca', lastname: 'Sartori')

            def wp1 = workPackageService.create(
                    failOnError: true,
                    usernameCreated: 'super',
                    code: 'Project-One',
                    description: 'This is project ONE',
                    validFrom: LocalDate.now(),
                    validTo: LocalDate.now().plusYears(10),
            )
            def wp2 = workPackageService.create(
                    failOnError: true,
                    usernameCreated: 'super',
                    code: 'Project-Two',
                    description: 'This is project TWO',
                    validFrom: LocalDate.now(),
                    validTo: LocalDate.now().plusYears(10),
            )

            workAssignmentService.create(
                    failOnError: true,
                    usernameCreated: 'super',
                    workPackage: wp1,
                    individual: james,
                    validFrom: LocalDate.now(),
                    validTo: LocalDate.now().plusYears(10),
            )

            workAssignmentService.create(
                    failOnError: true,
                    usernameCreated: 'super',
                    workPackage: wp2,
                    individual: gianluca,
                    validFrom: LocalDate.now(),
                    validTo: LocalDate.now().plusYears(10),
            )

        }

        applicationService.onDevInstall { String tenantId ->
        }

        applicationService.beforeInit {

        }

        applicationService.onInit {
            registerPrettyPrinter(TBusinessPartner, '${it.firstname} ${it.lastname}')
            registerPrettyPrinter(TWorkPackage, '${it.code}')

            // Main application features
            applicationService.registerFeature(
                    controller: 'workAssignment',
                    icon: 'fa-id-card-clip',
                    favourite: true,
            )
        }

        applicationService.onTenantInit {

        }

        applicationService.afterInit { String tenantId ->

        }

        securityService.afterLogin { String tenantId, GrailsHttpSession session ->

        }

        securityService.afterLogout { String tenantId ->

        }
    }

    def destroy = {
    }

}
