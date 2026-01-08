'use client'

import {
	createLesson,
	deleteLesson,
	fetchEarmarks,
	fetchGroups,
	fetchLessons,
	fetchSubjects,
	fetchTeachers,
	Lesson,
	updateLesson,
} from '@/lib/api/scheduleApi'
import {
	Add as AddIcon,
	Delete as DeleteIcon,
	Edit as EditIcon,
} from '@mui/icons-material'
import {
	Box,
	Button,
	Chip,
	Dialog,
	DialogActions,
	DialogContent,
	DialogTitle,
	FormControl,
	Grid,
	IconButton,
	InputLabel,
	MenuItem,
	OutlinedInput,
	Paper,
	Select,
	SelectChangeEvent,
	TextField,
	Typography,
} from '@mui/material'
import { DataGrid, GridColDef } from '@mui/x-data-grid'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { useState } from 'react'

export default function LessonView() {
	const queryClient = useQueryClient()
	const [selectedLessonId, setSelectedLessonId] = useState<number | null>(null)

	// Data Fetching
	const { data: lessons = [], isLoading: isLoadingLessons } = useQuery({
		queryKey: ['lessons'],
		queryFn: fetchLessons,
	})
	const { data: subjects = [] } = useQuery({
		queryKey: ['subjects'],
		queryFn: fetchSubjects,
	})
	const { data: earmarks = [] } = useQuery({
		queryKey: ['earmarks'],
		queryFn: fetchEarmarks,
	})
	const { data: teachers = [] } = useQuery({
		queryKey: ['teachers'],
		queryFn: fetchTeachers,
	})
	const { data: groups = [] } = useQuery({
		queryKey: ['groups'],
		queryFn: fetchGroups,
	})

	// Mutations
	const createLessonMutation = useMutation({
		mutationFn: createLesson,
		onSuccess: () => queryClient.invalidateQueries({ queryKey: ['lessons'] }),
	})
	const updateLessonMutation = useMutation({
		mutationFn: (data: { id: number; lesson: Partial<Lesson> }) =>
			updateLesson(data.id, data.lesson),
		onSuccess: () => queryClient.invalidateQueries({ queryKey: ['lessons'] }),
	})
	const deleteLessonMutation = useMutation({
		mutationFn: deleteLesson,
		onSuccess: () => {
			queryClient.invalidateQueries({ queryKey: ['lessons'] })
			setSelectedLessonId(null)
		},
	})

	// UI State
	const [openDialog, setOpenDialog] = useState(false)
	const [dialogMode, setDialogMode] = useState<'create' | 'edit'>('create')
	const [formData, setFormData] = useState<Partial<Lesson>>({
		count: 2,
		teacherIds: [],
		groupIds: [],
	})

	const handleAdd = () => {
		setFormData({ count: 2, teacherIds: [], groupIds: [] })
		setDialogMode('create')
		setOpenDialog(true)
	}

	const handleEdit = () => {
		const lesson = lessons.find(l => l.id === selectedLessonId)
		if (lesson) {
			setFormData({
				...lesson,
				teacherIds: lesson.teacherIds || [],
				groupIds: lesson.groupIds || [],
			})
			setDialogMode('edit')
			setOpenDialog(true)
		}
	}

	const handleDelete = () => {
		if (selectedLessonId && confirm('Delete this lesson?')) {
			deleteLessonMutation.mutate(selectedLessonId)
		}
	}

	const handleSubmit = () => {
		if (dialogMode === 'create') {
			createLessonMutation.mutate(formData)
		} else if (selectedLessonId) {
			updateLessonMutation.mutate({ id: selectedLessonId, lesson: formData })
		}
		setOpenDialog(false)
	}

	const columns: GridColDef[] = [
		{ field: 'subjectName', headerName: 'Subject', flex: 1 },
		{ field: 'earmarkName', headerName: 'Earmark', width: 120 },
		{
			field: 'teacherNames',
			headerName: 'Teachers',
			flex: 1,
			valueGetter: (value: string[]) => (value ? value.join(', ') : ''),
		},
		{
			field: 'groupNames',
			headerName: 'Groups',
			flex: 1,
			valueGetter: (value: string[]) => (value ? value.join(', ') : ''),
		},
		{ field: 'count', headerName: 'Count', width: 70 },
	]

	return (
		<Grid container spacing={2} sx={{ height: '100%' }}>
			<Grid
				item
				xs={12}
				sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}
			>
				<Box
					sx={{
						display: 'flex',
						justifyContent: 'space-between',
						alignItems: 'center',
						mb: 1,
					}}
				>
					<Typography variant='subtitle1'>Lessons</Typography>
					<Box>
						<IconButton size='small' onClick={handleAdd}>
							<AddIcon />
						</IconButton>
						<IconButton
							size='small'
							disabled={!selectedLessonId}
							onClick={handleEdit}
						>
							<EditIcon />
						</IconButton>
						<IconButton
							size='small'
							disabled={!selectedLessonId}
							onClick={handleDelete}
						>
							<DeleteIcon />
						</IconButton>
					</Box>
				</Box>
				<Paper sx={{ flexGrow: 1 }}>
					<DataGrid
						rows={lessons}
						columns={columns}
						loading={isLoadingLessons}
						onRowClick={params => setSelectedLessonId(params.row.id as number)}
						density='compact'
						hideFooter
						getRowClassName={params =>
							params.row.id === selectedLessonId ? 'Mui-selected' : ''
						}
						sx={{
							'& .Mui-selected': {
								bgcolor: 'primary.light',
								'&:hover': { bgcolor: 'primary.light' },
							},
						}}
					/>
				</Paper>
			</Grid>

			<Dialog
				open={openDialog}
				onClose={() => setOpenDialog(false)}
				maxWidth='sm'
				fullWidth
			>
				<DialogTitle>
					{dialogMode === 'create' ? 'Add Lesson' : 'Edit Lesson'}
				</DialogTitle>
				<DialogContent>
					<Box sx={{ display: 'flex', flexDirection: 'column', gap: 2, mt: 1 }}>
						{/* Subject */}
						<FormControl fullWidth size='small'>
							<InputLabel>Subject</InputLabel>
							<Select
								value={formData.subjectId || ''}
								label='Subject'
								onChange={e =>
									setFormData({
										...formData,
										subjectId: Number(e.target.value),
									})
								}
							>
								{subjects.map(s => (
									<MenuItem key={s.id} value={s.id}>
										{s.name}
									</MenuItem>
								))}
							</Select>
						</FormControl>

						{/* Earmark */}
						<FormControl fullWidth size='small'>
							<InputLabel>Earmark</InputLabel>
							<Select
								value={formData.earmarkId || ''}
								label='Earmark'
								onChange={e =>
									setFormData({
										...formData,
										earmarkId: Number(e.target.value),
									})
								}
							>
								{earmarks.map(e => (
									<MenuItem key={e.id} value={e.id}>
										{e.name} ({e.size})
									</MenuItem>
								))}
							</Select>
						</FormControl>

						{/* Teachers (Multi-select) */}
						<FormControl fullWidth size='small'>
							<InputLabel>Teachers</InputLabel>
							<Select
								multiple
								value={formData.teacherIds || []}
								onChange={(e: SelectChangeEvent<number[]>) => {
									const value = e.target.value
									setFormData({
										...formData,
										teacherIds:
											typeof value === 'string'
												? value.split(',').map(Number)
												: value,
									})
								}}
								input={<OutlinedInput label='Teachers' />}
								renderValue={selected => (
									<Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 0.5 }}>
										{selected.map(value => {
											const teacher = teachers.find(t => t.id === value)
											return (
												<Chip key={value} label={teacher?.name} size='small' />
											)
										})}
									</Box>
								)}
							>
								{teachers.map(t => (
									<MenuItem key={t.id} value={t.id}>
										{t.name}
									</MenuItem>
								))}
							</Select>
						</FormControl>

						{/* Groups (Multi-select) */}
						<FormControl fullWidth size='small'>
							<InputLabel>Groups</InputLabel>
							<Select
								multiple
								value={formData.groupIds || []}
								onChange={(e: SelectChangeEvent<number[]>) => {
									const value = e.target.value
									setFormData({
										...formData,
										groupIds:
											typeof value === 'string'
												? value.split(',').map(Number)
												: value,
									})
								}}
								input={<OutlinedInput label='Groups' />}
								renderValue={selected => (
									<Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 0.5 }}>
										{selected.map(value => {
											const group = groups.find(g => g.id === value)
											return (
												<Chip key={value} label={group?.name} size='small' />
											)
										})}
									</Box>
								)}
							>
								{groups.map(g => (
									<MenuItem key={g.id} value={g.id}>
										{g.name} ({g.size})
									</MenuItem>
								))}
							</Select>
						</FormControl>

						{/* Count */}
						<TextField
							margin='dense'
							label='Count'
							type='number'
							fullWidth
							size='small'
							value={formData.count || ''}
							onChange={e =>
								setFormData({ ...formData, count: parseInt(e.target.value) })
							}
						/>
					</Box>
				</DialogContent>
				<DialogActions>
					<Button onClick={() => setOpenDialog(false)}>Cancel</Button>
					<Button onClick={handleSubmit}>Save</Button>
				</DialogActions>
			</Dialog>
		</Grid>
	)
}
