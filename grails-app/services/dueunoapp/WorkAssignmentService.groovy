package dueunoapp

import dueuno.audit.AuditOperation
import dueuno.audit.AuditService
import dueuno.security.SecurityService
import grails.gorm.DetachedCriteria
import grails.gorm.multitenancy.CurrentTenant
import grails.gorm.transactions.Transactional
import groovy.contracts.Requires
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import jakarta.annotation.PostConstruct

import java.time.LocalDateTime

@Slf4j
@CurrentTenant
@CompileStatic
class WorkAssignmentService {

    SecurityService securityService
    AuditService auditService

    @PostConstruct
    void init() {
    }

    @CompileDynamic
    private DetachedCriteria<TWorkAssignment> buildQuery(Map filterParams) {
        def query = TWorkAssignment.where {}

        if (filterParams.containsKey('id')) query = query.where { id == filterParams.id }
        if (filterParams.containsKey('individual')) query = query.where { individual.id == filterParams.individual }
        if (filterParams.containsKey('workPackage.code')) query = query.where {
            workPackage.code == filterParams.'workPackage.code'
        }
        if (filterParams.containsKey('validFrom')) query = query.where { validFrom == filterParams.validFrom }
        if (filterParams.containsKey('validTo')) query = query.where { validTo == filterParams.validTo }

//        if (filterParams.find) {
//            String search = filterParams.find.replaceAll('\\*', '%')
//            query = query.where {
//                code =~ "%${search}%"
//            }
//        }

        return query
    }

    private Map getFetchAll() {
        return [
                'workPackage': 'join',
                'individual' : 'join',
        ]
    }

    private Map getFetch() {
        return [
                'workPackage': 'join',
                'individual' : 'join',
        ]
    }

    TWorkAssignment get(Serializable id, Boolean softDeleted = false) {
        return find(id: id, softDeleted: softDeleted)
    }

    TWorkAssignment find(Map filterParams) {
        return buildQuery(filterParams).get(fetch: fetchAll)
    }

    List<TWorkAssignment> list(Map filterParams = [:], Map fetchParams = [:]) {
        if (!fetchParams.sort) fetchParams.sort = [dateCreated: 'desc']
        if (!fetchParams.fetch) fetchParams.fetch = fetch

        def query = buildQuery(filterParams)
        return query.list(fetchParams)
    }

    Number count(Map filterParams = [:]) {
        def query = buildQuery(filterParams)
        return query.count()
    }

    @Transactional
    TWorkAssignment create(Map args = [:]) {
        if (args.usernameCreated == null) args.usernameCreated = securityService.currentUsername
        if (args.failOnError == null) args.failOnError = false

        TWorkAssignment obj = new TWorkAssignment(args)
        obj.save(flush: true, failOnError: args.failOnError)
        return obj
    }

    @Transactional
    @CompileDynamic
    @Requires({ args.id })
    TWorkAssignment update(Map args = [:]) {
        if (args.failOnError == null) args.failOnError = false

        TWorkAssignment obj = get(args.id)
        obj.properties = args
        obj.save(flush: true, failOnError: args.failOnError)
        return obj
    }

    @Transactional
    void delete(Serializable id) {
        TWorkAssignment obj = get(id)
        obj.delete(flush: true, failOnError: true)
        auditService.log(AuditOperation.DELETE, obj)
    }

}
