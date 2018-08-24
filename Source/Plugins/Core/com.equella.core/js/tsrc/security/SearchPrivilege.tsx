/* import { Button, Checkbox, CircularProgress, FormControlLabel, Paper, Theme, Typography } from '@material-ui/core';
import List from '@material-ui/core/List';
import withStyles, { StyleRules, WithStyles } from '@material-ui/core/styles/withStyles';
import AddIcon from '@material-ui/icons/Add';
import * as React from 'react';
import { connect, Dispatch } from 'react-redux';
import { TargetList } from '../api/acleditor';
//import { Privilege } from '../api';
import { Bridge } from '../api/bridge';
import AppBarQuery from '../components/AppBarQuery';
import ConfirmDialog from '../components/ConfirmDialog';
import SearchResult from '../components/SearchResult';
//import { searchPrivileges } from '../service/acl';
import { courseService } from '../services';
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

interface SearchPrivilegeProps extends WithStyles<'results' | 'overall' | 'fab' 
    | 'resultHeader' | 'resultText' | 'resultList' | 'progress'> {
    bridge: Bridge;
    deletePrivilege: (id: string) => Promise<{id:string}>;
    checkCreate: () => Promise<boolean>;
}

interface SearchPrivilegeState {
    query: string;
    confirmOpen: boolean;
    canCreate: boolean;
    includeArchived: boolean;
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

export const strings = prepLangStrings("courses", {
    title: "Privileges",
    sure: "Are you sure you want to delete - '%s'?", 
    confirmDelete: "It will be permanently deleted.", 
    coursesAvailable: {
        zero: "No courses available",
        one: "%d course",
        more: "%d courses"
    }, 
    includeArchived: "Include archived",
    archived: "Archived"
});

class SearchPrivilege extends React.Component<SearchPrivilegeProps, SearchPrivilegeState> {

    constructor(props: SearchPrivilegeProps){
        super(props);
        this.state = {
            query: "",
            confirmOpen: false,
            canCreate: false,
            includeArchived: false,
            acls: {entries:[]},
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
        const {resumptionToken,searching, query, includeArchived, acls} = this.state;
        if (resumptionToken && !searching && acls.entries.length < MaxPrivileges)
        {
            this.doSearch(query, includeArchived, false);
        }
    }

    nextSearch : NodeJS.Timer | null = null;

    doSearch = (q: string, includeArchived: boolean, reset: boolean) => {
        const resumptionToken = reset ? undefined : this.state.resumptionToken;
        const doReset = resumptionToken == undefined;
        const { bottomVisible } = this.state;
        this.setState({searching:true});
        searchPrivileges(q, includeArchived, resumptionToken, 30).then(sr => {
            if (sr.resumptionToken && bottomVisible) setTimeout(this.maybeKeepSearching, 250);
            this.setState((prevState) => ({...prevState, 
                courses: doReset ? sr.results : prevState.courses.concat(sr.results), 
                totalAvailable: sr.available, 
                resumptionToken: sr.resumptionToken, 
                searching: false
            }));
        });
    }

    searchFromState = () => {
        const {query,includeArchived} = this.state;
        this.doSearch(query, includeArchived, true);
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
        this.doSearch("", false, true);
        this.props.checkCreate().then(canCreate => this.setState({canCreate}));
    }

    handleArchived = (includeArchived: boolean) => {
        const {query} = this.state;
        this.setState({includeArchived})
        this.doSearch(query, includeArchived, true)
    }

    handleClose = () => {
        this.setState({confirmOpen:false});
    }

    handleDelete = () => {
        if (this.state.deleteDetails) {
            const { uuid } = this.state.deleteDetails;
            this.handleClose();
            const {includeArchived, query} = this.state;
            this.props.deletePrivilege(uuid).then(
                _ => this.doSearch(query, includeArchived, true)
            );
        }
    }

    render() {
        const {routes,router, Template} = this.props.bridge;
        const {classes} = this.props;
        const {query,confirmOpen,canCreate,acls,totalAvailable,searching} = this.state;
        //const {onClick:clickNew, href:hrefNew} = router(routes.NewPrivilege)
        return <Template title={strings.title} titleExtra={<AppBarQuery query={query} onChange={this.handleQuery}/>}>
            <div className={classes.overall}>
                {this.state.deleteDetails && 
                    <ConfirmDialog open={confirmOpen} 
                        title={sprintf(strings.sure, this.state.deleteDetails.name)} 
                        onConfirm={this.handleDelete} onCancel={this.handleClose}>
                        {strings.confirmDelete}
                    </ConfirmDialog>}
                <Paper className={classes.results}>
                    <div className={classes.resultHeader}>
                        <Typography className={classes.resultText} variant="subheading">{
                            acls.entries.length == 0 ? strings.coursesAvailable.zero : 
                            sprintf(sizedString(totalAvailable||0, strings.coursesAvailable), totalAvailable||0)
                        }</Typography>
                        <FormControlLabel 
                        control={<Checkbox onChange={(e,includeArchived) => this.handleArchived(includeArchived)}/>} 
                            label={strings.includeArchived}/>
                    </div>
                    <List className={classes.resultList}>
                    {
                    acls.entries.map((entry) => {
							return <div></div>
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

function mapStateToProps(state: StoreState) {
    return {};
}

function mapDispatchToProps(dispatch: Dispatch<any>) {
    const { workers } = courseService;
    return {
        deletePrivilege: (id: string) => workers.delete(dispatch, {id}), 
        checkCreate: () => workers.checkPrivs(dispatch, {privilege:["CREATE_COURSE_INFO"]}).then(p => p.indexOf("CREATE_COURSE_INFO") != -1)
    }
}

export default withStyles(styles)(connect(mapStateToProps, mapDispatchToProps)(SearchPrivilege)); */