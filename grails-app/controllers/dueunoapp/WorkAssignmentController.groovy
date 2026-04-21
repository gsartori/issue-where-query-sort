package dueunoapp

import dueuno.commons.utils.LogUtils
import dueuno.elements.ElementsController
import dueuno.elements.components.TableRow
import dueuno.elements.contents.ContentCreate
import dueuno.elements.contents.ContentEdit
import dueuno.elements.contents.ContentTable
import dueuno.elements.controls.DateField
import dueuno.elements.controls.Select
import grails.plugin.springsecurity.annotation.Secured
import groovy.util.logging.Slf4j
import jakarta.annotation.PostConstruct

@Slf4j
@Secured(['ROLE_USER'])
class WorkAssignmentController implements ElementsController {

    WorkPackageService workPackageService
    WorkAssignmentService workAssignmentService
    BusinessPartnerService businessPartnerService

    @PostConstruct
    void init() {
        // Executes only once when the application starts
    }

    def handleException(Exception e) {
        log.error LogUtils.logStackTrace(e)
        display exception: e
    }

    def index() {
        def c = createContent(ContentTable)
        c.table.with {
            filters.with {
                addField(
                        class: Select,
                        id: 'individual',
                        optionsFromRecordset: businessPartnerService.list(),
                        cols: 3,
                )
                addField(
                        class: Select,
                        id: 'workPackage.code',
                        optionsFromList: workPackageService.list()*.code,
                        renderTextPrefix: false,
                        cols: 3,
                )
                addField(
                        class: DateField,
                        id: 'validFrom',
                        cols: 3,
                )
                addField(
                        class: DateField,
                        id: 'validTo',
                        cols: 3,
                )
            }
            sortable = [
                    'individual'      : 'asc',
                    'workPackage.code': 'asc',
                    'validFrom'       : 'desc',
                    'validTo'         : 'desc',
            ]
            columns = [
                    'individual',
                    'workPackage.code',
                    'workPackage.description',
                    'validFrom',
                    'validTo',
                    'pippo.it',
            ]

            body.eachRow { TableRow row, Map values ->
            }
        }

        c.table.body = workAssignmentService.list(c.table.filterParams, c.table.fetchParams)
        c.table.paginate = workAssignmentService.count(c.table.filterParams)

        display content: c
    }

    private buildForm(TWorkAssignment obj = null, Boolean readonly = false) {
        def c = obj
                ? createContent(ContentEdit)
                : createContent(ContentCreate)

        if (readonly) {
            c.header.removeNextButton()
            c.form.readonly = true
        }

        c.form.with {
            validate = TWorkAssignment
            addField(
                    class: Select,
                    id: 'workPackage',
                    optionsFromRecordset: workPackageService.list(),
            )
            addField(
                    class: Select,
                    id: 'individual',
                    optionsFromRecordset: businessPartnerService.list(),
                    cols: 6,
            )
            addField(
                    class: DateField,
                    id: 'validFrom',
                    cols: 3,
            )
            addField(
                    class: DateField,
                    id: 'validTo',
                    cols: 3,
            )
        }

        if (obj) {
            c.form.values = obj
        }

        return c
    }

    def create() {
        def c = buildForm()
        display content: c, modal: true
    }

    def onCreate() {
        def obj = workAssignmentService.create(params)
        if (obj.hasErrors()) {
            display errors: obj
            return
        }

        display action: 'index'
    }

    def edit() {
        def obj = workAssignmentService.get(params.id)
        def c = buildForm(obj)
        display content: c, modal: true
    }

    def onEdit() {
        def obj = workAssignmentService.update(params)
        if (obj.hasErrors()) {
            display errors: obj
            return
        }

        display action: 'index'
    }

    def onDelete() {
        try {
            workAssignmentService.delete(params.id)
            display action: 'index'

        } catch (e) {
            display exception: e
        }
    }
}
