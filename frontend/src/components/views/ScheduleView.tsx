'use client'

import {
	fetchGroups,
	fetchGroupSchedule,
	fetchTeachers,
	fetchTeacherSchedule,
} from '@/lib/api/scheduleApi'
import {
	Autocomplete,
	Box,
	Grid,
	Paper,
	TextField,
	ToggleButton,
	ToggleButtonGroup,
} from '@mui/material'
import { DataGrid, GridColDef } from '@mui/x-data-grid'
import { useQuery } from '@tanstack/react-query'
import { useState } from 'react'

export default function ScheduleView() {
	const [mode, setMode] = useState<'teacher' | 'group'>('teacher')
	const [selectedId, setSelectedId] = useState<number | null>(null)

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
		queryKey: ['schedule', mode, selectedId],
		queryFn: () =>
			mode === 'teacher'
				? fetchTeacherSchedule(selectedId!)
				: fetchGroupSchedule(selectedId!),
		enabled: !!selectedId,
	})

	const columns: GridColDef[] = [
		{ field: 'dayName', headerName: 'Day', width: 120 },
		{ field: 'timeStart', headerName: 'Start', width: 90 },
		{ field: 'timeEnd', headerName: 'End', width: 90 },
		{ field: 'subjectName', headerName: 'Subject', flex: 1 },
		{ field: 'earmarkName', headerName: 'Type', width: 100 },
		{ field: 'auditoriumName', headerName: 'Auditorium', width: 100 },
		{
			field: 'additionalInfo',
			headerName: mode === 'teacher' ? 'Groups' : 'Teachers',
			flex: 1,
		},
	]

	// Client-side mapping for unique IDs required by DataGrid
	const rows = schedule.map((entry, index) => ({ id: index, ...entry }))

	return (
		<Grid
			container
			spacing={2}
			sx={{ height: '100%', flexDirection: 'column' }}
		>
			<Grid>
				<Box sx={{ display: 'flex', gap: 2, alignItems: 'center' }}>
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
						<ToggleButton value='teacher'>Teacher</ToggleButton>
						<ToggleButton value='group'>Group</ToggleButton>
					</ToggleButtonGroup>

					<Box sx={{ width: 300 }}>
						{mode === 'teacher' ? (
							<Autocomplete
								options={teachers}
								getOptionLabel={option => option.name}
								renderInput={params => (
									<TextField {...params} label='Select Teacher' size='small' />
								)}
								onChange={(e, value) => setSelectedId(value?.id || null)}
								value={teachers.find(t => t.id === selectedId) || null}
							/>
						) : (
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
					</Box>
				</Box>
			</Grid>

			<Grid sx={{ flexGrow: 1 }}>
				<Paper sx={{ height: '100%' }}>
					<DataGrid
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
