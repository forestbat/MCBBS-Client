import React from "react";
import Reflux from "reflux";
import { withStyles } from "@material-ui/core/styles";

import ForumPanel from "../utils/forumPanel";

import db from "../../../../native/electron/localScripts/localDatabase/database";

import TestData from "../testData";

const styles = theme => ({
  padding: {
    padding: "8px"
  }
});

class MainPage extends Reflux.Component {
  render() {
    const { classes } = this.props;

    return (
      <div>
        {
          Object.keys(TestData.mainPage.forumGroups).map(n => (
            <ForumPanel
              key={TestData.mainPage.forumGroups[n].forumGroupId}
              forumGroupName={TestData.mainPage.forumGroups[n].forumGroupName}
              forums={TestData.mainPage.forumGroups[n].forums}
            />
          ))
        }
      </div>
    )
  }
}

export default withStyles(styles, { withTheme: true })(MainPage);