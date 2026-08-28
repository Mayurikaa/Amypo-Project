import { configureStore } from '@reduxjs/toolkit';
import authReducer from './slices/authSlice';
import projectInitiativeReducer from './slices/projectInitiativeSlice';
import projectMilestoneReducer from './slices/projectMilestoneSlice';
import projectTaskReducer from './slices/projectTaskSlice';
import systemAccountReducer from './slices/systemAccountSlice';
import taskSubmissionReducer from './slices/taskSubmissionSlice';

export default configureStore({
  reducer: {
    auth: authReducer,
    projectInitiatives: projectInitiativeReducer,
    projectMilestones: projectMilestoneReducer,
    projectTasks: projectTaskReducer,
    systemAccounts: systemAccountReducer,
    taskSubmissions: taskSubmissionReducer,
  },
});
