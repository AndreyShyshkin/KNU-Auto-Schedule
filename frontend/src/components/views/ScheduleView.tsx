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
	Grid,
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
} from '@mui/material'
import { DataGrid, GridColDef, useGridApiRef } from '@mui/x-data-grid'
import { useQuery } from '@tanstack/react-query'
import { useState, useEffect } from 'react'
import FileDownloadIcon from '@mui/icons-material/FileDownload'
import PictureAsPdfIcon from '@mui/icons-material/PictureAsPdf'

export default function ScheduleView() {
	const [mode, setMode] = useState<'teacher' | 'group' | 'all'>('all')
	const [selectedId, setSelectedId] = useState<number | null>(null)
	const [selectedVersionId, setSelectedVersionId] = useState<number | ''>('')
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
		{ field: 'dayName', headerName: 'Day', width: 120 },
		{ field: 'timeStart', headerName: 'Start', width: 90 },
		{ field: 'timeEnd', headerName: 'End', width: 90 },
		{ field: 'subjectName', headerName: 'Subject', flex: 1 },
		{ field: 'lessonTypeName', headerName: 'Lesson Type', width: 130 },
		{ field: 'buildingName', headerName: 'Building', width: 120 },
		{ field: 'earmarkName', headerName: 'Room Type', width: 100 },
		{ 
			field: 'auditoriumName', 
			headerName: 'Auditorium', 
			width: 150,
			renderCell: (params) => {
				const value = params.value as string
				if (value?.includes('http')) {
					// Витягуємо саме посилання, якщо є префікс "Метод: "
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
			headerName: mode === 'teacher' ? 'Groups' : mode === 'group' ? 'Teachers' : 'Teachers | Groups',
			flex: 1,
		},
	]

	// Client-side mapping for unique IDs required by DataGrid
	const rows = schedule.map((entry, index) => ({ id: index, ...entry }))

	const getExportData = () => {
		// Use apiRef to get filtered and sorted rows
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
		<Grid
			container
			spacing={2}
			sx={{ height: '100%', flexDirection: 'column' }}
		>
			<Grid>
				<Box sx={{ display: 'flex', gap: 2, alignItems: 'center', flexWrap: 'wrap' }}>
					<FormControl size='small' sx={{ minWidth: 200 }}>
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
						<ToggleButton value='all'>All</ToggleButton>
						<ToggleButton value='teacher'>Teacher</ToggleButton>
						<ToggleButton value='group'>Group</ToggleButton>
					</ToggleButtonGroup>

					<Box sx={{ width: 300 }}>
						{mode === 'teacher' && (
							<Autocomplete
								options={teachers}
								getOptionLabel={option => option.name}
								renderInput={params => (
									<TextField {...params} label='Select Teacher' size='small' />
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
									<TextField {...params} label='Select Group' size='small' />
								)}
								onChange={(e, value) => setSelectedId(value?.id || null)}
								value={groups.find(g => g.id === selectedId) || null}
							/>
						)}
						{mode === 'all' && (
							<Typography variant="body2" color="text.secondary">
								Показ повного розкладу для версії
							</Typography>
						)}
					</Box>

					<Box sx={{ flexGrow: 1 }} />

					<Box sx={{ display: 'flex', gap: 1 }}>
						<Button
							variant="outlined"
							size="small"
							startIcon={<FileDownloadIcon />}
							onClick={handleExportExcel}
							disabled={rows.length === 0}
						>
							Excel
						</Button>
						<Button
							variant="outlined"
							size="small"
							color="secondary"
							startIcon={<PictureAsPdfIcon />}
							onClick={handleExportPdf}
							disabled={rows.length === 0}
						>
							PDF
						</Button>
					</Box>
				</Box>
			</Grid>

			<Grid sx={{ flexGrow: 1 }}>
				<Paper sx={{ height: '100%' }}>
					<DataGrid
						apiRef={apiRef}
						rows={rows}
						columns={columns}
						loading={isLoading}
						density='compact'
						hideFooter
						initialState={{
							sorting: {
								sortModel: [{ field: 'dayName', sort: 'asc' }],
							},
						}}
					/>
				</Paper>
			</Grid>
		</Grid>
	)
}
