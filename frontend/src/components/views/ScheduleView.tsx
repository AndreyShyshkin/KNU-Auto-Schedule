'use client'

import {
	fetchGroups,
	fetchGroupSchedule,
	fetchTeachers,
	fetchTeacherSchedule,
	fetchScheduleVersions,
	fetchAllSchedule,
	exportScheduleExcel,
	exportSchedulePdf,
	ScheduleVersion,
} from '@/lib/api/scheduleApi'
import {
	Autocomplete,
	Box,
	Paper,
	TextField,
	ToggleButton,
	ToggleButtonGroup,
	FormControl,
	InputLabel,
	Select,
	MenuItem,
	Typography,
	Button,
	Chip,
} from '@mui/material'
import { DataGrid, GridColDef, useGridApiRef } from '@mui/x-data-grid'
import { useQuery } from '@tanstack/react-query'
import { useState, useEffect, useMemo } from 'react'
import FileDownloadIcon from '@mui/icons-material/FileDownload'
import PictureAsPdfIcon from '@mui/icons-material/PictureAsPdf'
import TodayIcon from '@mui/icons-material/Today'
import DateRangeIcon from '@mui/icons-material/DateRange'

interface ScheduleViewProps {
	initialMode?: 'teacher' | 'group' | 'all'
	initialId?: number | null
}

export default function ScheduleView({ initialMode = 'all', initialId = null }: ScheduleViewProps) {
	const [mode, setMode] = useState<'teacher' | 'group' | 'all'>(initialMode)
	const [selectedId, setSelectedId] = useState<number | null>(initialId)
	const [selectedVersionId, setSelectedVersionId] = useState<number | ''>('')
	const [filterToday, setFilterToday] = useState(true)
	const apiRef = useGridApiRef()

	const { data: versions = [] } = useQuery({
		queryKey: ['scheduleVersions'],
		queryFn: fetchScheduleVersions,
	})

	useEffect(() => {
		if (versions.length > 0 && selectedVersionId === '') {
			const current = versions.find(v => v.current)
			if (current) {
				setSelectedVersionId(current.id)
			} else {
				setSelectedVersionId(versions[0].id)
			}
		}
	}, [versions, selectedVersionId])

	const { data: teachers = [] } = useQuery({
		queryKey: ['teachers'],
		queryFn: fetchTeachers,
		enabled: mode === 'teacher',
	})
	const { data: groups = [] } = useQuery({
		queryKey: ['groups'],
		queryFn: fetchGroups,
		enabled: mode === 'group',
	})

	const { data: schedule = [], isLoading } = useQuery({
		queryKey: ['schedule', mode, selectedId, selectedVersionId],
		queryFn: () => {
			if (mode === 'all') return fetchAllSchedule(selectedVersionId !== '' ? (selectedVersionId as number) : undefined)
			return mode === 'teacher'
				? fetchTeacherSchedule(selectedId!, selectedVersionId !== '' ? (selectedVersionId as number) : undefined)
				: fetchGroupSchedule(selectedId!, selectedVersionId !== '' ? (selectedVersionId as number) : undefined)
		},
		enabled: selectedVersionId !== '' && (mode === 'all' || !!selectedId),
	})

	const columns: GridColDef[] = [
		{ field: 'actualDate', headerName: 'Дата', width: 110 },
		{ field: 'dayName', headerName: 'День', width: 100 },
		{ field: 'timeStart', headerName: 'Початок', width: 80 },
		{ field: 'timeEnd', headerName: 'Кінець', width: 80 },
		{ field: 'subjectName', headerName: 'Предмет', minWidth: 250, flex: 1 },
		{ field: 'lessonTypeName', headerName: 'Тип', width: 130 },
		{ field: 'buildingName', headerName: 'Корпус', width: 120 },
		{ field: 'earmarkName', headerName: 'Тип ауд.', width: 100 },
		{ 
			field: 'auditoriumName', 
			headerName: 'Аудиторія', 
			width: 150,
			renderCell: (params) => {
				const value = params.value as string
				if (value?.includes('http')) {
					const linkMatch = value.match(/https?:\/\/[^\s]+/);
					if (linkMatch) {
						return (
							<a 
								href={linkMatch[0]} 
								target="_blank" 
								rel="noopener noreferrer"
								style={{ color: '#1976d2', textDecoration: 'underline' }}
							>
								{value}
							</a>
						)
					}
				}
				return value
			}
		},
		{
			field: 'additionalInfo',
			headerName: mode === 'teacher' ? 'Групи' : mode === 'group' ? 'Викладачі' : 'Викладачі | Групи',
			minWidth: 300,
			flex: 1,
		},
	]

	const todayStr = useMemo(() => new Date().toISOString().split('T')[0], [])

	const filteredRows = useMemo(() => {
		let rows = schedule.map((entry, index) => ({ id: index, ...entry }))
		if (filterToday) {
			rows = rows.filter(r => r.actualDate === todayStr)
		}
		return rows
	}, [schedule, filterToday, todayStr])

	const getExportData = () => {
		const api = apiRef.current
		if (!api) return []
		const visibleRowIds = api.getSortedRowIds()
		return visibleRowIds.map(id => {
			const row = api.getRow(id)
			const { id: _, ...data } = row
			return data
		})
	}

	const handleExportExcel = async () => {
		const data = getExportData()
		const title = `Розклад - ${mode === 'all' ? 'Всі' : mode === 'teacher' ? teachers.find(t => t.id === selectedId)?.name : groups.find(g => g.id === selectedId)?.name}`
		await exportScheduleExcel(data, title)
	}

	const handleExportPdf = async () => {
		const data = getExportData()
		const title = `Розклад - ${mode === 'all' ? 'Всі' : mode === 'teacher' ? teachers.find(t => t.id === selectedId)?.name : groups.find(g => g.id === selectedId)?.name}`
		await exportSchedulePdf(data, title)
	}

	return (
		<Box sx={{ display: 'flex', flexDirection: 'column', height: '100%', width: '100%', gap: 2 }}>
			{/* Header with Filters */}
			<Box sx={{ display: 'flex', gap: 2, alignItems: 'center', flexWrap: 'wrap', p: 1 }}>
				<FormControl size='small' sx={{ minWidth: 220 }}>
					<InputLabel>Версія розкладу</InputLabel>
					<Select
						value={selectedVersionId}
						label='Версія розкладу'
						onChange={(e) => setSelectedVersionId(e.target.value as number)}
					>
						{versions.map((v) => (
							<MenuItem key={v.id} value={v.id}>
								{v.name} {v.current ? '(Поточна)' : ''}
							</MenuItem>
						))}
					</Select>
				</FormControl>
				
				<ToggleButtonGroup
					value={mode}
					exclusive
					onChange={(e, newMode) => {
						if (newMode) {
							setMode(newMode)
							setSelectedId(null)
						}
					}}
					size='small'
				>
					<ToggleButton value='all'>Всі</ToggleButton>
					<ToggleButton value='teacher'>Викладач</ToggleButton>
					<ToggleButton value='group'>Група</ToggleButton>
				</ToggleButtonGroup>

				<Box sx={{ width: 300 }}>
					{mode === 'teacher' && (
						<Autocomplete
							options={teachers}
							getOptionLabel={option => option.name}
							renderInput={params => (
								<TextField {...params} label='Виберіть викладача' size='small' />
							)}
							onChange={(e, value) => setSelectedId(value?.id || null)}
							value={teachers.find(t => t.id === selectedId) || null}
						/>
					)}
					{mode === 'group' && (
						<Autocomplete
							options={groups}
							getOptionLabel={option => option.name}
							renderInput={params => (
								<TextField {...params} label='Виберіть групу' size='small' />
							)}
							onChange={(e, value) => setSelectedId(value?.id || null)}
							value={groups.find(g => g.id === selectedId) || null}
						/>
					)}
				</Box>

				<Box sx={{ display: 'flex', gap: 1, ml: 2 }}>
					<Chip 
						icon={<TodayIcon />} 
						label="На сьогодні" 
						onClick={() => setFilterToday(true)}
						color={filterToday ? "primary" : "default"}
						variant={filterToday ? "filled" : "outlined"}
					/>
					<Chip 
						icon={<DateRangeIcon />} 
						label="Весь період" 
						onClick={() => setFilterToday(false)}
						color={!filterToday ? "primary" : "default"}
						variant={!filterToday ? "filled" : "outlined"}
					/>
				</Box>

				<Box sx={{ flexGrow: 1 }} />

				<Box sx={{ display: 'flex', gap: 1 }}>
					<Button
						variant="outlined"
						size="small"
						startIcon={<FileDownloadIcon />}
						onClick={handleExportExcel}
						disabled={filteredRows.length === 0}
					>
						Excel
					</Button>
					<Button
						variant="outlined"
						size="small"
						color="secondary"
						startIcon={<PictureAsPdfIcon />}
						onClick={handleExportPdf}
						disabled={filteredRows.length === 0}
					>
						PDF
					</Button>
				</Box>
			</Box>

			{filteredRows.length === 0 && !isLoading && (
				<Box sx={{ p: 4, textAlign: 'center', bgcolor: 'background.paper', borderRadius: 2 }}>
					<Typography variant="h6" color="text.secondary">
						{filterToday ? "На сьогодні занять немає" : "Розклад порожній"}
					</Typography>
					{filterToday && (
						<Button sx={{ mt: 2 }} variant="outlined" onClick={() => setFilterToday(false)}>
							Показати весь період
						</Button>
					)}
				</Box>
			)}

			{/* Table area */}
			<Box sx={{ flexGrow: 1, minHeight: 0, width: '100%', display: filteredRows.length > 0 ? 'block' : 'none' }}>
				<Paper sx={{ height: '100%', borderRadius: 2 }}>
					<DataGrid
						apiRef={apiRef}
						rows={filteredRows}
						columns={columns}
						loading={isLoading}
						density='compact'
						disableRowSelectionOnClick
						initialState={{
							sorting: {
								sortModel: [{ field: 'actualDate', sort: 'asc' }],
							},
							pagination: { paginationModel: { pageSize: 100 } }
						}}
						sx={{
							border: 'none',
							'& .MuiDataGrid-columnHeaders': {
								bgcolor: 'rgba(0,0,0,0.02)',
								fontWeight: 'bold',
							},
						}}
					/>
				</Paper>
			</Box>
		</Box>
	)
}
