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
class WorkPackageService {

    SecurityService securityService
    AuditService auditService

    @PostConstruct
    void init() {
    }

    @CompileDynamic
    private DetachedCriteria<TWorkPackage> buildQuery(Map filterParams) {
        def query = TWorkPackage.where {}

        if (filterParams.containsKey('id')) query = query.where { id == filterParams.id }

        if (filterParams.find) {
            String search = filterParams.find.replaceAll('\\*', '%')
            query = query.where {
                code =~ "%${search}%"
            }
        }

        return query
    }

    private Map getFetchAll() {
        return [
                :
        ]
    }

    private Map getFetch() {
        return [
                :
        ]
    }

    TWorkPackage get(Serializable id, Boolean softDeleted = false) {
        return find(id: id, softDeleted: softDeleted)
    }

    TWorkPackage find(Map filterParams) {
        return buildQuery(filterParams).get(fetch: fetchAll)
    }

    List<TWorkPackage> list(Map filterParams = [:], Map fetchParams = [:]) {
        if (!fetchParams.sort) fetchParams.sort = [code: 'asc']
        if (!fetchParams.fetch) fetchParams.fetch = fetch

        def query = buildQuery(filterParams)
        return query.list(fetchParams)
    }

    Number count(Map filterParams = [:]) {
        def query = buildQuery(filterParams)
        return query.count()
    }

    @Transactional
    TWorkPackage create(Map args = [:]) {
        if (args.usernameCreated == null) args.usernameCreated = securityService.currentUsername
        if (args.failOnError == null) args.failOnError = false

        TWorkPackage obj = new TWorkPackage(args)
        obj.save(flush: true, failOnError: args.failOnError)
        return obj
    }

    @Transactional
    @CompileDynamic
    @Requires({ args.id })
    TWorkPackage update(Map args = [:]) {
        if (args.failOnError == null) args.failOnError = false

        TWorkPackage obj = get(args.id)
        obj.properties = args
        obj.save(flush: true, failOnError: args.failOnError)
        return obj
    }

    @Transactional
    void delete(Serializable id) {
        TWorkPackage obj = get(id)
        obj.delete(flush: true, failOnError: true)
        auditService.log(AuditOperation.DELETE, obj)
    }

}
