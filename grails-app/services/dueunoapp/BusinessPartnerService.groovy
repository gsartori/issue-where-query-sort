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
class BusinessPartnerService {

    SecurityService securityService
    AuditService auditService

    @PostConstruct
    void init() {
        // Executes only once when the application starts
    }

    @CompileDynamic
    private DetachedCriteria<TBusinessPartner> buildQuery(Map filterParams) {
        def query = TBusinessPartner.where {}

        if (filterParams.containsKey('id')) query = query.where { id == filterParams.id }

        if (filterParams.find) {
            String search = filterParams.find.replaceAll('\\*', '%')
            query = query.where {
                true
                        || lastname =~ "%${search}%"
                        || firstname =~ "%${search}%"
            }
        }

        // Add additional filters here

        return query
    }

    private Map getFetchAll() {
        // Add any relationship here (Eg. references to other DomainObjects or hasMany)
        return [
                :
                // hasMany relationships
        ]
    }

    private Map getFetch() {
        // Add only single-sided relationships here (Eg. references to other Domain Objects)
        // DO NOT add hasMany relationships, you are going to have troubles with pagination
        return [
                :
        ]
    }

    TBusinessPartner get(Serializable id, Boolean softDeleted = false) {
        return find(id: id, softDeleted: softDeleted)
    }

    TBusinessPartner find(Map filterParams) {
        return buildQuery(filterParams).get(fetch: fetchAll)
    }

    List<TBusinessPartner> list(Map filterParams = [:], Map fetchParams = [:]) {
        filterParams.all = false
        return listAll(filterParams, fetchParams)
    }

    Number count(Map filterParams = [:]) {
        filterParams.all = false
        return countAll(filterParams)
    }

    List<TBusinessPartner> listAll(Map filterParams = [:], Map fetchParams = [:]) {
        if (filterParams.all == null) filterParams.all = true

        if (!fetchParams.sort) fetchParams.sort = [dateCreated: 'desc']
        if (!fetchParams.fetch) fetchParams.fetch = fetch

        def query = buildQuery(filterParams)
        return query.list(fetchParams)
    }

    Number countAll(Map filterParams = [:]) {
        if (filterParams.all == null) filterParams.all = true

        def query = buildQuery(filterParams)
        return query.count()
    }

    @Transactional
    TBusinessPartner create(Map args = [:]) {
        if (args.usernameCreated == null) args.usernameCreated = securityService.currentUsername
        if (args.failOnError == null) args.failOnError = false

        TBusinessPartner obj = new TBusinessPartner(args)
        obj.save(flush: true, failOnError: args.failOnError)
        return obj
    }

    @Transactional
    @CompileDynamic
    @Requires({ args.id })
    TBusinessPartner update(Map args = [:]) {
        if (args.usernameUpdated == null) args.usernameUpdated = securityService.currentUsername
        if (args.failOnError == null) args.failOnError = false

        TBusinessPartner obj = get(args.id)
        obj.properties = args
        obj.save(flush: true, failOnError: args.failOnError)

        return obj
    }

    @Transactional
    void delete(Serializable id) {
        TBusinessPartner obj = get(id)
            obj.delete(flush: true, failOnError: true)
            auditService.log(AuditOperation.DELETE, obj)
    }

}
