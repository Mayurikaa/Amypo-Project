import React, { useState } from 'react';
import { useSelector } from 'react-redux';
import ErrorHandler from './components/ErrorHandler';
import Login from './components/Login';
import Navbar from './components/layout/Navbar';
import StatCards from './components/dashboard/StatCards';
import DomainChart from './components/dashboard/DomainChart';
import RecentActivity from './components/dashboard/RecentActivity';
import ProjectInitiativeList from './components/projectInitiative/ProjectInitiativeList';
import ProjectMilestoneList from './components/projectMilestone/ProjectMilestoneList';
import ProjectTaskList from './components/projectTask/ProjectTaskList';
import TaskSubmissionList from './components/taskSubmission/TaskSubmissionList';
import SystemAccountList from './components/systemAccount/SystemAccountList';

const views = {
  initiatives: ProjectInitiativeList,
  milestones: ProjectMilestoneList,
  tasks: ProjectTaskList,
  submissions: TaskSubmissionList,
  accounts: SystemAccountList,
};

function Dashboard() {
  const user = useSelector((state) => state.auth.user);
  const showAnalytics = ['PROJECT_DIRECTOR', 'PROJECT_MANAGER'].includes(user?.domainRole);

  return (
    <main className="dashboard-shell">
      <section className="welcome-panel">
        <p className="eyebrow">Operational control center</p>
        <h1>Keep every delivery signal in view.</h1>
        <p>Track initiatives, milestones, workload, and audit submissions from one secure workspace.</p>
      </section>
      {showAnalytics && <StatCards />}
      {showAnalytics && <section className="dashboard-grid"><DomainChart /><RecentActivity /></section>}
    </main>
  );
}

export default function App() {
  const { isAuthenticated } = useSelector((state) => state.auth);
  const [activeTab, setActiveTab] = useState('home');
  if (!isAuthenticated) return <ErrorHandler><Login /></ErrorHandler>;

  const View = views[activeTab];
  return (
    <ErrorHandler>
      <Navbar activeTab={activeTab} onNavigate={setActiveTab} />
      {activeTab === 'home' ? <Dashboard /> : <main className="content-shell"><View /></main>}
    </ErrorHandler>
  );
}
