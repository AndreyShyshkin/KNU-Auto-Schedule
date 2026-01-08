'use client';

import { useState, useEffect } from 'react';
import axios from 'axios';
import { 
  Box, AppBar, Toolbar, Typography, Tabs, Tab, Paper, Button, CircularProgress 
} from '@mui/material';
import { startBuild, getBuildStatus } from '@/lib/api/scheduleApi';
import ChairView from '@/components/views/ChairView';
import SpecialityView from '@/components/views/SpecialityView';
import TeacherView from '@/components/views/TeacherView';
import GroupView from '@/components/views/GroupView';
import SubjectView from '@/components/views/SubjectView';
import PlacementView from '@/components/views/PlacementView';
import DateView from '@/components/views/DateView';
import LessonView from '@/components/views/LessonView';
import ScheduleView from '@/components/views/ScheduleView';

interface TabPanelProps {
  children?: React.ReactNode;
  index: number;
  value: number;
}

function TabPanel(props: TabPanelProps) {
  const { children, value, index, ...other } = props;

  return (
    <div
      role="tabpanel"
      hidden={value !== index}
      id={`simple-tabpanel-${index}`}
      aria-labelledby={`simple-tab-${index}`}
      {...other}
      style={{ height: '100%', overflow: 'auto' }}
    >
      {value === index && (
        <Box sx={{ p: 2, height: '100%' }}>
          {children}
        </Box>
      )}
    </div>
  );
}

export default function Home() {
  const [value, setValue] = useState(0);
  const [rightTab, setRightTab] = useState(0);
  const [isBuilding, setIsBuilding] = useState(false);

  const handleChange = (event: React.SyntheticEvent, newValue: number) => {
    setValue(newValue);
  };

  const handleBuild = async () => {
    try {
      await startBuild();
      setIsBuilding(true);
    } catch (error) {
      console.error('Failed to start build', error);
    }
  };

  useEffect(() => {
    let interval: NodeJS.Timeout;
    if (isBuilding) {
      interval = setInterval(async () => {
        try {
          const status = await getBuildStatus();
          setIsBuilding(status);
          if (!status) {
            alert('Build finished!');
          }
        } catch (e) {
          console.error(e);
        }
      }, 1000);
    }
    return () => clearInterval(interval);
  }, [isBuilding]);

  return (
    <Box sx={{ flexGrow: 1, height: '100vh', display: 'flex', flexDirection: 'column' }}>
      <AppBar position="static" color="default" elevation={1}>
        <Toolbar variant="dense">
          <Typography variant="h6" color="inherit" component="div" sx={{ flexGrow: 1 }}>
            KNU Schedule
          </Typography>
        </Toolbar>
      </AppBar>

      <Box sx={{ flexGrow: 1, display: 'flex', overflow: 'hidden' }}>
        {/* Left Side: Input Pane (Tabs) */}
        <Box sx={{ width: '70%', display: 'flex', flexDirection: 'column', borderRight: 1, borderColor: 'divider' }}>
          <Box sx={{ borderBottom: 1, borderColor: 'divider' }}>
            <Tabs value={value} onChange={handleChange} variant="scrollable" scrollButtons="auto">
              <Tab label="Date" />
              <Tab label="Chair" />
              <Tab label="Speciality" />
              <Tab label="Teacher" />
              <Tab label="Group" />
              <Tab label="Placement" />
              <Tab label="Subject" />
              <Tab label="Lesson" />
            </Tabs>
          </Box>
          
          <Box sx={{ flexGrow: 1, overflow: 'auto', bgcolor: 'background.paper' }}>
            <TabPanel value={value} index={0}>
              <DateView />
            </TabPanel>
            <TabPanel value={value} index={1}>
              <ChairView />
            </TabPanel>
            <TabPanel value={value} index={2}>
              <SpecialityView />
            </TabPanel>
            <TabPanel value={value} index={3}>
              <TeacherView />
            </TabPanel>
            <TabPanel value={value} index={4}>
              <GroupView />
            </TabPanel>
            <TabPanel value={value} index={5}>
              <PlacementView />
            </TabPanel>
            <TabPanel value={value} index={6}>
              <SubjectView />
            </TabPanel>
            <TabPanel value={value} index={7}>
              <LessonView />
            </TabPanel>
          </Box>
        </Box>

        {/* Right Side: Build Pane / Results */}
        <Box sx={{ width: '30%', display: 'flex', flexDirection: 'column', borderLeft: 1, borderColor: 'divider' }}>
          <Box sx={{ borderBottom: 1, borderColor: 'divider' }}>
            <Tabs value={rightTab} onChange={(e, v) => setRightTab(v)} variant="fullWidth">
              <Tab label="Build" />
              <Tab label="Results" />
            </Tabs>
          </Box>

          <Box sx={{ flexGrow: 1, p: 2, overflow: 'auto', bgcolor: '#fafafa' }}>
            {rightTab === 0 && (
              <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
                <Typography variant="h6" gutterBottom>Build Schedule</Typography>
                <Typography variant="body2" color="text.secondary" paragraph align="center">
                  Click the button below to start the scheduling algorithm.
                </Typography>
                
// In Build tab:
                <Button 
                  variant="contained" 
                  onClick={handleBuild} 
                  disabled={isBuilding}
                  sx={{ mt: 2 }}
                >
                  {isBuilding ? 'Building...' : 'Build Schedule'}
                </Button>

                <Button 
                  variant="outlined" 
                  color="error"
                  onClick={async () => {
                    if (confirm('Clear all generated results?')) {
                      await axios.delete('/api/schedule/clear');
                      alert('Results cleared');
                    }
                  }} 
                  disabled={isBuilding}
                  sx={{ mt: 1 }}
                >
                  Clear Results
                </Button>
                
                {isBuilding && <CircularProgress sx={{ mt: 2 }} />}
              </Box>
            )}

            {rightTab === 1 && (
              <ScheduleView />
            )}
          </Box>
        </Box>
      </Box>
    </Box>
  );
}