import React from 'react';
// Sử dụng Switch và Redirect thay vì Routes và Navigate
import { BrowserRouter as Router, Route, Switch, Redirect } from 'react-router-dom';
import ProjectList from './components/ProjectList'; 
import ProjectForm from './components/ProjectForm'; 
import ProjectDetail from './components/ProjectDetail';
import TeamPage from "./components/TeamPage";

function App() {
  return (
    <Router>
      <div>
        <Switch>
          {/* Tự động chuyển hướng từ "/" sang "/projects" khi mở trang web */}
          <Redirect exact from="/" to="/projects" />
          
          {/* Sử dụng component={...} thay vì element={<... />} */}
          <Route exact path="/projects" component={ProjectList} />
          
          <Route path="/projects/:id" component={ProjectDetail} />
          
          <Route path="/create-project" component={ProjectForm} />
          <Route path="/teams" component={TeamPage} />
        </Switch>
      </div>
    </Router>
  );
}

export default App;