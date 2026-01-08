'use client';

import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { 
  fetchDays, createDay, updateDay, deleteDay,
  fetchTimes, createTime, updateTime, deleteTime,
  Day, Time
} from '@/lib/api/scheduleApi';
import { DataGrid, GridColDef, GridRenderCellParams } from '@mui/x-data-grid';
import { 
  Typography, Paper, Grid, Box, IconButton, Dialog, DialogTitle, DialogContent, 
  DialogActions, Button, TextField, Checkbox 
} from '@mui/material';
import { Add as AddIcon, Edit as EditIcon, Delete as DeleteIcon } from '@mui/icons-material';

export default function DateView() {
  const queryClient = useQueryClient();
  const [selectedDayId, setSelectedDayId] = useState<number | null>(null);
  const [selectedTimeId, setSelectedTimeId] = useState<number | null>(null);

  // --- Days ---
  const { data: days = [], isLoading: isLoadingDays } = useQuery({
    queryKey: ['days'],
    queryFn: fetchDays,
  });

  const createDayMutation = useMutation({
    mutationFn: createDay,
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['days'] }),
  });

  const updateDayMutation = useMutation({
    mutationFn: (data: { id: number; day: Partial<Day> }) => updateDay(data.id, data.day),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['days'] }),
  });

  const deleteDayMutation = useMutation({
    mutationFn: deleteDay,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['days'] });
      setSelectedDayId(null);
    },
  });

  // --- Times ---
  const { data: times = [], isLoading: isLoadingTimes } = useQuery({
    queryKey: ['times'],
    queryFn: fetchTimes,
  });

  const createTimeMutation = useMutation({
    mutationFn: createTime,
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['times'] }),
  });

  const updateTimeMutation = useMutation({
    mutationFn: (data: { id: number; time: Partial<Time> }) => updateTime(data.id, data.time),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['times'] }),
  });

  const deleteTimeMutation = useMutation({
    mutationFn: deleteTime,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['times'] });
      setSelectedTimeId(null);
    },
  });

  // --- UI State ---
  const [openDayDialog, setOpenDayDialog] = useState(false);
  const [dayDialogMode, setDayDialogMode] = useState<'create' | 'edit'>('create');
  const [dayFormData, setDayFormData] = useState<Partial<Day>>({});

  const [openTimeDialog, setOpenTimeDialog] = useState(false);
  const [timeDialogMode, setTimeDialogMode] = useState<'create' | 'edit'>('create');
  const [timeFormData, setTimeFormData] = useState<Partial<Time>>({});

  // Handlers Day
  const handleAddDay = () => {
    setDayFormData({});
    setDayDialogMode('create');
    setOpenDayDialog(true);
  };

  const handleEditDay = () => {
    const day = days.find(d => d.id === selectedDayId);
    if (day) {
      setDayFormData(day);
      setDayDialogMode('edit');
      setOpenDayDialog(true);
    }
  };

  const handleDeleteDay = () => {
    if (selectedDayId && confirm('Delete this day?')) {
      deleteDayMutation.mutate(selectedDayId);
    }
  };

  const handleSubmitDay = () => {
    if (dayDialogMode === 'create') {
      createDayMutation.mutate(dayFormData);
    } else if (selectedDayId) {
      updateDayMutation.mutate({ id: selectedDayId, day: dayFormData });
    }
    setOpenDayDialog(false);
  };

  // Handlers Time
  const handleAddTime = () => {
    setTimeFormData({});
    setTimeDialogMode('create');
    setOpenTimeDialog(true);
  };

  const handleEditTime = () => {
    const time = times.find(t => t.id === selectedTimeId);
    if (time) {
      setTimeFormData(time);
      setTimeDialogMode('edit');
      setOpenTimeDialog(true);
    }
  };

  const handleDeleteTime = () => {
    if (selectedTimeId && confirm('Delete this time?')) {
      deleteTimeMutation.mutate(selectedTimeId);
    }
  };

  const handleSubmitTime = () => {
    if (timeDialogMode === 'create') {
      createTimeMutation.mutate(timeFormData);
    } else if (selectedTimeId) {
      updateTimeMutation.mutate({ id: selectedTimeId, time: timeFormData });
    }
    setOpenTimeDialog(false);
  };

  // Binding Logic
  const handleToggleTimeForDay = (timeId: number) => {
    if (!selectedDayId) return;
    const day = days.find(d => d.id === selectedDayId);
    if (!day) return;

    const currentTimes = day.times || [];
    const exists = currentTimes.find(t => t.id === timeId);
    let newTimes;

    if (exists) {
      newTimes = currentTimes.filter(t => t.id !== timeId);
    } else {
      const timeToAdd = times.find(t => t.id === timeId);
      if (timeToAdd) {
        newTimes = [...currentTimes, timeToAdd];
      } else {
        newTimes = currentTimes;
      }
    }

    updateDayMutation.mutate({ 
      id: selectedDayId, 
      day: { ...day, times: newTimes } 
    });
  };

  const dayColumns: GridColDef[] = [
    { field: 'name', headerName: 'Day Name', flex: 1 },
  ];

  const timeColumns: GridColDef[] = [
    {
      field: 'belong',
      headerName: 'Belong',
      width: 70,
      renderCell: (params: GridRenderCellParams) => {
        const day = days.find(d => d.id === selectedDayId);
        const isChecked = day?.times?.some(t => t.id === params.row.id) || false;
        return (
          <Checkbox
            checked={isChecked}
            disabled={!selectedDayId}
            onChange={() => handleToggleTimeForDay(params.row.id as number)}
          />
        );
      },
    },
    { field: 'start', headerName: 'Start Time', flex: 1 },
    { field: 'end', headerName: 'End Time', flex: 1 },
  ];

  return (
    <Grid container spacing={2} sx={{ height: '100%' }}>
      {/* Left Pane: Days */}
      <Grid item xs={6} sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 1 }}>
          <Typography variant="subtitle1">Days</Typography>
          <Box>
            <IconButton size="small" onClick={handleAddDay}><AddIcon /></IconButton>
            <IconButton size="small" disabled={!selectedDayId} onClick={handleEditDay}><EditIcon /></IconButton>
            <IconButton size="small" disabled={!selectedDayId} onClick={handleDeleteDay}><DeleteIcon /></IconButton>
          </Box>
        </Box>
        <Paper sx={{ flexGrow: 1 }}>
          <DataGrid
            rows={days}
            columns={dayColumns}
            loading={isLoadingDays}
            onRowClick={(params) => setSelectedDayId(params.row.id as number)}
            density="compact"
            hideFooter
            getRowClassName={(params) => params.row.id === selectedDayId ? 'Mui-selected' : ''}
            sx={{ '& .Mui-selected': { bgcolor: 'primary.light', '&:hover': { bgcolor: 'primary.light' } } }}
          />
        </Paper>
      </Grid>

      {/* Right Pane: Times */}
      <Grid item xs={6} sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 1 }}>
          <Typography variant="subtitle1">
            {selectedDayId ? 'Times (Select to Bind)' : 'Times'}
          </Typography>
          <Box>
            <IconButton size="small" onClick={handleAddTime}><AddIcon /></IconButton>
            <IconButton size="small" disabled={!selectedTimeId} onClick={handleEditTime}><EditIcon /></IconButton>
            <IconButton size="small" disabled={!selectedTimeId} onClick={handleDeleteTime}><DeleteIcon /></IconButton>
          </Box>
        </Box>
        <Paper sx={{ flexGrow: 1 }}>
          <DataGrid
            rows={times}
            columns={timeColumns}
            loading={isLoadingTimes}
            onRowClick={(params) => setSelectedTimeId(params.row.id as number)}
            density="compact"
            hideFooter
            getRowClassName={(params) => params.row.id === selectedTimeId ? 'Mui-selected' : ''}
            sx={{ '& .Mui-selected': { bgcolor: 'primary.light', '&:hover': { bgcolor: 'primary.light' } } }}
          />
        </Paper>
      </Grid>

      {/* Day Dialog */}
      <Dialog open={openDayDialog} onClose={() => setOpenDayDialog(false)}>
        <DialogTitle>{dayDialogMode === 'create' ? 'Add Day' : 'Edit Day'}</DialogTitle>
        <DialogContent>
          <TextField
            autoFocus
            margin="dense"
            label="Name"
            fullWidth
            value={dayFormData.name || ''}
            onChange={(e) => setDayFormData({ ...dayFormData, name: e.target.value })}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenDayDialog(false)}>Cancel</Button>
          <Button onClick={handleSubmitDay}>Save</Button>
        </DialogActions>
      </Dialog>

      {/* Time Dialog */}
      <Dialog open={openTimeDialog} onClose={() => setOpenTimeDialog(false)}>
        <DialogTitle>{timeDialogMode === 'create' ? 'Add Time' : 'Edit Time'}</DialogTitle>
        <DialogContent>
          <TextField
            autoFocus
            margin="dense"
            label="Start Time"
            fullWidth
            value={timeFormData.start || ''}
            onChange={(e) => setTimeFormData({ ...timeFormData, start: e.target.value })}
          />
          <TextField
            margin="dense"
            label="End Time"
            fullWidth
            value={timeFormData.end || ''}
            onChange={(e) => setTimeFormData({ ...timeFormData, end: e.target.value })}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenTimeDialog(false)}>Cancel</Button>
          <Button onClick={handleSubmitTime}>Save</Button>
        </DialogActions>
      </Dialog>
    </Grid>
  );
}