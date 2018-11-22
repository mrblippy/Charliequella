import { CircularProgress, Paper, Theme, Typography } from '@material-ui/core';
import List from '@material-ui/core/List';
import withStyles, { StyleRules, WithStyles } from '@material-ui/core/styles/withStyles';
import * as React from 'react';
import { connect, Dispatch } from 'react-redux';
import { sprintf } from 'sprintf-js';
import { TargetList } from '../api/acleditor';
import AppBarQuery from '../components/AppBarQuery';
import TargetListEntry from '../components/TargetListEntry';
import aclService from '../service/acl';
import { EditObjectDispatchProps, EditObjectProps, EditObjectStateProps } from '../service/generic';
import { StoreState } from '../store';
import { prepLangStrings, sizedString } from '../util/langstrings';
import VisibilitySensor = require('react-visibility-sensor');

const styles = (theme: Theme) => ({
    overall: {
      padding: theme.spacing.unit * 2, 
      height: "100%"
    }, 
    results: {
      padding: theme.spacing.unit * 2, 
      position: "relative"
    }, 
    resultList: {
    },
    fab: {
      zIndex: 1000,
      position: 'fixed',
      bottom: theme.spacing.unit * 2,
      right: theme.spacing.unit * 5,
    }, 
    resultHeader: {
      display: "flex",
      justifyContent: "flex-end"
    }, 
    resultText: {
        flexGrow: 1
    }, 
    progress: {
        display: "flex", 
        justifyContent: "center"
    }
} as StyleRules)

interface SearchPrivilegeStateProps extends EditObjectStateProps<TargetList> {
    
}

interface SearchPrivilegeDispatchProps extends EditObjectDispatchProps<TargetList> {
    //listPrivileges: (node: string) => Promise<{node: string, result: string[]}>;
}

interface SearchPrivilegeProps extends EditObjectProps, 
                        SearchPrivilegeStateProps, 
                        SearchPrivilegeDispatchProps {
	id: string;
}

type Props = SearchPrivilegeProps & WithStyles<'results' | 'overall' | 'fab' 
    | 'resultHeader' | 'resultText' | 'resultList' | 'progress'>;

interface SearchPrivilegeState {
    query: string;
    confirmOpen: boolean;
    canCreate: boolean;
    searching: boolean;
    totalAvailable?: number;
    resumptionToken?: string;
    bottomVisible: boolean;
    acls: TargetList;
    deleteDetails?: {
        uuid: string;
        name: string;
    }
}

const MaxPrivileges = 200;

export const strings = prepLangStrings("security", {
    title: "Privileges",
    sure: "Are you sure you want to delete - '%s'?", 
    confirmDelete: "It will be permanently deleted.", 
    rulesAvailable: {
        zero: "No rules available",
        one: "%d rule",
        more: "%d rules"
    }
});

class SearchPrivilege extends React.Component<Props, SearchPrivilegeState> {

    constructor(props: Props){
        super(props);
        this.state = {
            query: "",
            confirmOpen: false,
            canCreate: false,
            acls: {entries:[], node: ''},
            searching: false, 
            bottomVisible: true
        }
    }

    maybeKeepSearching = () => {
        if (this.state.bottomVisible) 
        {
            this.fetchMore();
        }
    }

    fetchMore = () => {
        const {resumptionToken,searching, query, acls} = this.state;
        if (resumptionToken && !searching && acls.entries.length < MaxPrivileges)
        {
            this.doSearch(query, false);
        }
    }

    nextSearch : NodeJS.Timer | null = null;

    doSearch = (q: string, reset: boolean) => {
        //const resumptionToken = reset ? undefined : this.state.resumptionToken;
        //const doReset = resumptionToken == undefined;
        //const { node, listPrivileges } = this.props;
        //const { bottomVisible } = this.state;
        this.setState({searching:true});
        const { loadObject } = this.props;
        loadObject('INSTITUTION').then(res => {
            this.setState((prevState) => ({...prevState, 
                //courses: doReset ? sr.results : prevState.courses.concat(sr.results), 
                acls: res.result,
                //totalAvailable: sr.available, 
                //resumptionToken: sr.resumptionToken, 
                searching: false
            }));
        });
        /*
        listPrivileges(node).then(sr => {
            //if (sr.resumptionToken && bottomVisible) setTimeout(this.maybeKeepSearching, 250);
            this.setState((prevState) => ({...prevState, 
                //courses: doReset ? sr.results : prevState.courses.concat(sr.results), 
                acls: {node, entries: sr.result.map((e) => {}) },
                //totalAvailable: sr.available, 
                //resumptionToken: sr.resumptionToken, 
                searching: false
            }));
        });*/
    }

    searchFromState = () => {
        const {query} = this.state;
        this.doSearch(query, true);
	}
	
    handleQuery = (q: string) => {
        this.setState({query:q});
        if (this.nextSearch)
        {
            clearTimeout(this.nextSearch);
        }
        this.nextSearch = setTimeout(this.searchFromState, 250);
    }

    visiblityCheck = (bottomVisible: boolean) => this.setState((prevState) => 
        ({...prevState, bottomVisible: prevState.bottomVisible && bottomVisible}))

    componentWillUnmount() {
        window.removeEventListener('scroll', this.onScroll, false);        
    }
  
    onScroll = () => {
        if ((window.innerHeight + window.scrollY) >= (document.body.offsetHeight - 400)) {
            this.fetchMore();
        }
    }

    componentDidMount() {
        window.addEventListener('scroll', this.onScroll, false);
        this.doSearch('', true);
        //this.props.checkCreate().then(canCreate => this.setState({canCreate}));
    }

    handleClose = () => {
        this.setState({confirmOpen:false});
    }
/*
    handleDelete = () => {
        if (this.state.deleteDetails) {
            const { uuid } = this.state.deleteDetails;
            this.handleClose();
            const {query} = this.state;
            this.props.deletePrivilege(uuid).then(
                _ => this.doSearch(query, true)
            );
        }
    }*/

    render() {
        const {Template} = this.props.bridge;
        const {classes} = this.props;
        const {query, acls, searching} = this.state;
        const totalAvailable = acls.entries.length;
        //const {onClick:clickNew, href:hrefNew} = router(routes.NewPrivilege)
/*
{this.state.deleteDetails && 
                    <ConfirmDialog open={confirmOpen} 
                        title={sprintf(strings.sure, this.state.deleteDetails.name)} 
                        onConfirm={this.handleDelete} onCancel={this.handleClose}>
                        {strings.confirmDelete}
                    </ConfirmDialog>}
*/

        return <Template title={strings.title} titleExtra={<AppBarQuery query={query} onChange={this.handleQuery}/>}>
            <div className={classes.overall}>
                
                <Paper className={classes.results}>
                    <div className={classes.resultHeader}>
                        <Typography className={classes.resultText} variant="subheading">{
                            acls.entries.length == 0 ? strings.rulesAvailable.zero : 
                            sprintf(sizedString(totalAvailable||0, strings.rulesAvailable), totalAvailable||0)
                        }</Typography>
                    </div>
                    <List className={classes.resultList}>
                    {
                    acls.entries.map((entry) => {
							return <TargetListEntry entry={entry} href='#' onClick={()=>{}} />
                        })
                    }
                    <VisibilitySensor onChange={this.visiblityCheck}/>
                    </List>
                    {searching && <div className={classes.progress}><CircularProgress/></div>}
                </Paper>
            </div>
        </Template>
    }
}

function mapStateToProps(state: StoreState): SearchPrivilegeStateProps {
    return { object: {entries: [], node: ''}};
}

function mapDispatchToProps(dispatch: Dispatch<any>): SearchPrivilegeDispatchProps {
    const { workers, actions } = aclService;
    return {
        //deletePrivilege: (id: string) => workers.delete(dispatch, {id}), 
        //listPrivileges: (node: string) => workers.listPrivileges(dispatch, {node})
        //,
        //checkCreate: () => workers.checkPrivs(dispatch, {privilege:["CREATE_COURSE_INFO"]}).then(p => p.indexOf("CREATE_COURSE_INFO") != -1)
        loadObject: (id: string) => workers.read(dispatch, {id}),
        saveObject: (object: TargetList) => workers.update(dispatch, { object }),
        modifyObject: (object: TargetList) => dispatch(actions.modify({ object })),
        validateObject: (object: TargetList) => workers.validate(dispatch, { object })
    };
}

export default withStyles(styles)(connect(mapStateToProps, mapDispatchToProps)(SearchPrivilege));