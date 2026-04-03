'use client'

import {
	createDay,
	createTime,
	Day,
	deleteDay,
	deleteTime,
	fetchDays,
	fetchTimes,
	fetchBuildings,
	handleError,
	Time,
	updateDay,
	updateTime,
	Building,
} from '@/lib/api/scheduleApi'
import {
	Add as AddIcon,
	Delete as DeleteIcon,
	Edit as EditIcon,
} from '@mui/icons-material'
import {
	Box,
	Button,
	Checkbox,
	Dialog,
	DialogActions,
	DialogContent,
	DialogTitle,
	Grid,
	IconButton,
	Paper,
	TextField,
	Typography,
	FormControl,
	InputLabel,
	Select,
	MenuItem,
} from '@mui/material'
import { DataGrid, GridColDef, GridRenderCellParams } from '@mui/x-data-grid'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { useState } from 'react'

export default function DateView() {
	const queryClient = useQueryClient()
	const [selectedDayId, setSelectedDayId] = useState<number | null>(null)
	const [selectedBuildingId, setSelectedBuildingId] = useState<number | null>(null)
	const [selectedTimeId, setSelectedTimeId] = useState<number | null>(null)

	const { data: buildings = [], isLoading: isLoadingBuildings } = useQuery({
		queryKey: ['buildings'],
		queryFn: fetchBuildings,
	})

	const { data: days = [], isLoading: isLoadingDays } = useQuery({
		queryKey: ['days'],
		queryFn: fetchDays,
	})

	const { data: times = [], isLoading: isLoadingTimes } = useQuery({
		queryKey: ['times'],
		queryFn: fetchTimes,
	})

	// --- Day Mutations ---
	const createDayMutation = useMutation({
		mutationFn: createDay,
		onSuccess: () => queryClient.invalidateQueries({ queryKey: ['days'] }),
		onError: (error) => alert(handleError(error)),
	})

	const updateDayMutation = useMutation({
		mutationFn: (data: { id: number; day: Partial<Day> }) =>
			updateDay(data.id, data.day),
		onSuccess: () => queryClient.invalidateQueries({ queryKey: ['days'] }),
		onError: (error) => alert(handleError(error)),
	})

	const deleteDayMutation = useMutation({
		mutationFn: deleteDay,
		onSuccess: () => {
			queryClient.invalidateQueries({ queryKey: ['days'] })
			setSelectedDayId(null)
		},
		onError: (error) => alert(handleError(error)),
	})

	// --- Time Mutations ---
	const createTimeMutation = useMutation({
		mutationFn: createTime,
		onSuccess: () => queryClient.invalidateQueries({ queryKey: ['times'] }),
		onError: (error) => alert(handleError(error)),
	})

	const updateTimeMutation = useMutation({
		mutationFn: (data: { id: number; time: Partial<Time> }) =>
			updateTime(data.id, data.time),
		onSuccess: () => queryClient.invalidateQueries({ queryKey: ['times'] }),
		onError: (error) => alert(handleError(error)),
	})

	const deleteTimeMutation = useMutation({
		mutationFn: deleteTime,
		onSuccess: () => {
			queryClient.invalidateQueries({ queryKey: ['times'] })
			setSelectedTimeId(null)
		},
		onError: (error) => alert(handleError(error)),
	})

	// --- UI State for Dialogs ---
	const [openDayDialog, setOpenDayDialog] = useState(false)
	const [dayDialogMode, setDayDialogMode] = useState<'create' | 'edit'>('create')
	const [dayFormData, setDayFormData] = useState<Partial<Day>>({})

	const [openTimeDialog, setOpenTimeDialog] = useState(false)
	const [timeDialogMode, setTimeDialogMode] = useState<'create' | 'edit'>('create')
	const [timeFormData, setTimeFormData] = useState<Partial<Time>>({})

	// --- Handlers Day ---
	const handleAddDay = () => {
		setDayFormData({})
		setDayDialogMode('create')
		setOpenDayDialog(true)
	}

	const handleEditDay = () => {
		const day = days.find(d => d.id === selectedDayId)
		if (day) {
			setDayFormData(day)
			setDayDialogMode('edit')
			setOpenDayDialog(true)
		}
	}

	const handleDeleteDay = () => {
		if (selectedDayId && confirm('Видалити цей день?')) {
			deleteDayMutation.mutate(selectedDayId)
		}
	}

	const handleSubmitDay = () => {
		if (dayDialogMode === 'create') {
			createDayMutation.mutate(dayFormData)
		} else if (selectedDayId) {
			updateDayMutation.mutate({ id: selectedDayId, day: dayFormData })
		}
		setOpenDayDialog(false)
	}

	// --- Handlers Time ---
	const handleAddTime = () => {
		setTimeFormData({ buildingId: selectedBuildingId || undefined })
		setTimeDialogMode('create')
		setOpenTimeDialog(true)
	}

	const handleEditTime = () => {
		const time = times.find(t => t.id === selectedTimeId)
		if (time) {
			setTimeFormData(time)
			setTimeDialogMode('edit')
			setOpenTimeDialog(true)
		}
	}

	const handleDeleteTime = () => {
		if (selectedTimeId && confirm('Видалити цей час?')) {
			deleteTimeMutation.mutate(selectedTimeId)
		}
	}

	const handleSubmitTime = () => {
		if (timeDialogMode === 'create') {
			createTimeMutation.mutate(timeFormData)
		} else if (selectedTimeId) {
			updateTimeMutation.mutate({ id: selectedTimeId, time: timeFormData })
		}
		setOpenTimeDialog(false)
	}

	const handleToggleTimeForDay = (timeId: number) => {
		if (!selectedDayId) return
		const day = days.find(d => d.id === selectedDayId)
		if (!day) return

		const currentTimes = day.times || []
		const exists = currentTimes.find(t => t.id === timeId)
		let newTimes

		if (exists) {
			newTimes = currentTimes.filter(t => t.id !== timeId)
		} else {
			const timeToAdd = times.find(t => t.id === timeId)
			if (timeToAdd) {
				newTimes = [...currentTimes, timeToAdd]
			} else {
				newTimes = currentTimes
			}
		}

		updateDayMutation.mutate({
			id: selectedDayId,
			day: { ...day, times: newTimes },
		})
	}

	const filteredTimes = selectedBuildingId 
		? times.filter(t => t.buildingId === selectedBuildingId)
		: []

	const dayColumns: GridColDef[] = [{ field: 'name', headerName: 'День', flex: 1 }]
	const buildingColumns: GridColDef[] = [{ field: 'name', headerName: 'Корпус', flex: 1 }]
	const timeColumns: GridColDef[] = [
		{
			field: 'belong',
			headerName: 'Є',
			width: 60,
			renderCell: (params: GridRenderCellParams) => {
				const day = days.find(d => d.id === selectedDayId)
				const isChecked = day?.times?.some(t => t.id === params.row.id) || false
				return (
					<Checkbox
						size="small"
						checked={isChecked}
						disabled={!selectedDayId}
						onChange={() => handleToggleTimeForDay(params.row.id as number)}
					/>
				)
			},
		},
		{ field: 'start', headerName: 'Початок', flex: 1 },
		{ field: 'end', headerName: 'Кінець', flex: 1 },
	]

	return (
		<Grid container spacing={2} sx={{ height: '100%' }}>
			{/* 1. Days */}
			<Grid size={4} sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
				<Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 0.5 }}>
					<Typography variant='subtitle2' sx={{ fontWeight: 'bold' }}>1. Дні</Typography>
					<Box>
						<IconButton size='small' onClick={handleAddDay}><AddIcon fontSize="small"/></IconButton>
						<IconButton size='small' disabled={!selectedDayId} onClick={handleEditDay}><EditIcon fontSize="small"/></IconButton>
						<IconButton size='small' disabled={!selectedDayId} onClick={handleDeleteDay}><DeleteIcon fontSize="small"/></IconButton>
					</Box>
				</Box>
				<Paper sx={{ flexGrow: 1 }}>
					<DataGrid
						rows={days}
						columns={dayColumns}
						loading={isLoadingDays}
						onRowClick={params => setSelectedDayId(params.row.id as number)}
						density='compact'
						hideFooter
						getRowClassName={params => params.row.id === selectedDayId ? 'Mui-selected' : ''}
						sx={{ '& .Mui-selected': { bgcolor: 'primary.light' } }}
					/>
				</Paper>
			</Grid>

			{/* 2. Buildings */}
			<Grid size={4} sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
				<Typography variant='subtitle2' gutterBottom sx={{ fontWeight: 'bold' }}>2. Оберіть Корпус</Typography>
				<Paper sx={{ flexGrow: 1 }}>
					<DataGrid
						rows={buildings}
						columns={buildingColumns}
						loading={isLoadingBuildings}
						onRowClick={params => setSelectedBuildingId(params.row.id as number)}
						density='compact'
						hideFooter
						getRowClassName={params => params.row.id === selectedBuildingId ? 'Mui-selected' : ''}
						sx={{ '& .Mui-selected': { bgcolor: 'secondary.light' } }}
					/>
				</Paper>
			</Grid>

			{/* 3. Times */}
			<Grid size={4} sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
				<Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 0.5 }}>
					<Typography variant='subtitle2' sx={{ fontWeight: 'bold' }}>3. Час (Прив'язка)</Typography>
					<Box>
						<IconButton size='small' disabled={!selectedBuildingId} onClick={handleAddTime}><AddIcon fontSize="small"/></IconButton>
						<IconButton size='small' disabled={!selectedTimeId} onClick={handleEditTime}><EditIcon fontSize="small"/></IconButton>
						<IconButton size='small' disabled={!selectedTimeId} onClick={handleDeleteTime}><DeleteIcon fontSize="small"/></IconButton>
					</Box>
				</Box>
				<Paper sx={{ flexGrow: 1 }}>
					<DataGrid
						rows={filteredTimes}
						columns={timeColumns}
						loading={isLoadingTimes}
						onRowClick={params => setSelectedTimeId(params.row.id as number)}
						density='compact'
						hideFooter
						getRowClassName={params => params.row.id === selectedTimeId ? 'Mui-selected' : ''}
					/>
				</Paper>
			</Grid>

			{/* Day Dialog */}
			<Dialog open={openDayDialog} onClose={() => setOpenDayDialog(false)}>
				<DialogTitle>{dayDialogMode === 'create' ? 'Додати день' : 'Редагувати день'}</DialogTitle>
				<DialogContent>
					<TextField
						autoFocus
						margin='dense'
						label='Назва дня'
						fullWidth
						value={dayFormData.name || ''}
						onChange={e => setDayFormData({ ...dayFormData, name: e.target.value })}
					/>
				</DialogContent>
				<DialogActions>
					<Button onClick={() => setOpenDayDialog(false)}>Скасувати</Button>
					<Button onClick={handleSubmitDay}>Зберегти</Button>
				</DialogActions>
			</Dialog>

			{/* Time Dialog */}
			<Dialog open={openTimeDialog} onClose={() => setOpenTimeDialog(false)}>
				<DialogTitle>{timeDialogMode === 'create' ? 'Додати час' : 'Редагувати час'}</DialogTitle>
				<DialogContent>
					<FormControl fullWidth margin="dense">
						<InputLabel>Корпус</InputLabel>
						<Select
							value={timeFormData.buildingId || ''}
							label="Корпус"
							onChange={e => setTimeFormData({ ...timeFormData, buildingId: e.target.value as number })}
						>
							{buildings.map(b => <MenuItem key={b.id} value={b.id}>{b.name}</MenuItem>)}
						</Select>
					</FormControl>
					<TextField
						margin='dense'
						label='Початок'
						fullWidth
						value={timeFormData.start || ''}
						onChange={e => setTimeFormData({ ...timeFormData, start: e.target.value })}
					/>
					<TextField
						margin='dense'
						label='Кінець'
						fullWidth
						value={timeFormData.end || ''}
						onChange={e => setTimeFormData({ ...timeFormData, end: e.target.value })}
					/>
				</DialogContent>
				<DialogActions>
					<Button onClick={() => setOpenTimeDialog(false)}>Скасувати</Button>
					<Button onClick={handleSubmitTime}>Зберегти</Button>
				</DialogActions>
			</Dialog>
		</Grid>
	)
}
