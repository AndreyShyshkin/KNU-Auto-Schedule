'use client'

import {
	createLesson,
	deleteLesson,
	fetchEarmarks,
	fetchGroups,
	fetchLessons,
	fetchSubjects,
	fetchTeachers,
	fetchBuildings,
	fetchAuditoriums,
	fetchLessonTypes,
	handleError,
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
	Checkbox,
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
	Divider,
	RadioGroup,
	FormControlLabel,
	Radio,
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
	const { data: buildings = [] } = useQuery({
		queryKey: ['buildings'],
		queryFn: fetchBuildings,
	})
	const { data: auditoriums = [] } = useQuery({
		queryKey: ['auditoriums'],
		queryFn: fetchAuditoriums,
	})
	const { data: lessonTypes = [] } = useQuery({
		queryKey: ['lessonTypes'],
		queryFn: fetchLessonTypes,
	})

	// Mutations
	const createLessonMutation = useMutation({
		mutationFn: createLesson,
		onSuccess: () => queryClient.invalidateQueries({ queryKey: ['lessons'] }),
		onError: (error) => alert(handleError(error)),
	})
	const updateLessonMutation = useMutation({
		mutationFn: (data: { id: number; lesson: Partial<Lesson> }) =>
			updateLesson(data.id, data.lesson),
		onSuccess: () => queryClient.invalidateQueries({ queryKey: ['lessons'] }),
		onError: (error) => alert(handleError(error)),
	})
	const deleteLessonMutation = useMutation({
		mutationFn: deleteLesson,
		onSuccess: () => {
			queryClient.invalidateQueries({ queryKey: ['lessons'] })
			setSelectedLessonId(null)
		},
		onError: (error) => alert(handleError(error)),
	})

	// UI State
	const [openDialog, setOpenDialog] = useState(false)
	const [dialogMode, setDialogMode] = useState<'create' | 'edit'>('create')
	const [formData, setFormData] = useState<Partial<Lesson>>({
		count: 2,
		teacherIds: [],
		groupIds: [],
		lessonTypeIds: [],
		online: false,
		onlineLink: '',
		allowMultipleAuditoriums: false,
		totalHours: 30,
		startDate: '',
		endDate: '',
	})

	const handleAdd = () => {
		setFormData({
			count: 2,
			teacherIds: [],
			groupIds: [],
			lessonTypeIds: [],
			online: false,
			onlineLink: '',
			allowMultipleAuditoriums: false,
			totalHours: 30,
			startDate: '',
			endDate: '',
		})
		setDialogMode('create')
		setOpenDialog(true)
	}

	const handleEdit = () => {
		const lesson = lessons.find((l) => l.id === selectedLessonId)
		if (lesson) {
			setFormData({
				...lesson,
				teacherIds: lesson.teacherIds || [],
				groupIds: lesson.groupIds || [],
				lessonTypeIds: lesson.lessonTypeIds || [],
				online: lesson.online || false,
				onlineLink: lesson.onlineLink || '',
				allowMultipleAuditoriums: lesson.allowMultipleAuditoriums || false,
				totalHours: lesson.totalHours || 30,
				startDate: lesson.startDate || '',
				endDate: lesson.endDate || '',
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

	// Logic for filtered selection in dialog
	const filteredEarmarks = formData.buildingId 
		? earmarks.filter(e => e.buildingId === formData.buildingId)
		: []

	const filteredAuditoriums = (formData.buildingId && formData.earmarkId)
		? auditoriums.filter(a => a.buildingId === formData.buildingId && a.earmarkId === formData.earmarkId)
		: []

	const columns: GridColDef[] = [
		{ field: 'subjectName', headerName: 'Предмет', flex: 1 },
		{ 
			field: 'lessonTypeNames', 
			headerName: 'Тип заняття', 
			width: 150,
			valueGetter: (value: string[]) => (value ? value.join(', ') : ''),
		},
		{ 
			field: 'location', 
			headerName: 'Місце проведення', 
			flex: 1,
			valueGetter: (value, row) => row.online ? `Онлайн: ${row.onlineLink}` : `${row.buildingName || ''} - ${row.auditoriumName || 'Авто'}`
		},
		{ field: 'earmarkName', headerName: 'Тип аудиторії', width: 120 },
		{
			field: 'teacherNames',
			headerName: 'Викладачі',
			flex: 1,
			valueGetter: (value: string[]) => (value ? value.join(', ') : ''),
		},
		{
			field: 'groupNames',
			headerName: 'Групи',
			flex: 1,
			valueGetter: (value: string[]) => (value ? value.join(', ') : ''),
		},
		{ field: 'totalHours', headerName: 'Годин', width: 80 },
	]

	return (
		<Grid container spacing={2} sx={{ height: '100%' }}>
			<Grid
				size={12}
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
					<Typography variant='subtitle1'>Заняття (Уроки)</Typography>
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
					{dialogMode === 'create' ? 'Додати заняття' : 'Редагувати заняття'}
				</DialogTitle>
				<DialogContent>
					<Box sx={{ display: 'flex', flexDirection: 'column', gap: 2, mt: 1 }}>
						{/* Subject */}
						<FormControl fullWidth size='small'>
							<InputLabel>Предмет</InputLabel>
							<Select
								value={formData.subjectId || ''}
								label='Предмет'
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

						{/* Lesson Types (Multi-select) */}
						<FormControl fullWidth size='small'>
							<InputLabel>Тип заняття (Лекція, Практика...)</InputLabel>
							<Select
								multiple
								value={(formData.lessonTypeIds || []) as any}
								onChange={(e: SelectChangeEvent<number[]>) => {
									const value = e.target.value
									setFormData({
										...formData,
										lessonTypeIds:
											typeof value === 'string'
												? value.split(',').map(Number)
												: value,
									})
								}}
								input={<OutlinedInput label='Тип заняття (Лекція, Практика...)' />}
								renderValue={(selected: any[]) => (
									<Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 0.5 }}>
										{selected.map(value => {
											const type = lessonTypes.find(t => t.id === value)
											return (
												<Chip key={value} label={type?.name} size='small' />
											)
										})}
									</Box>
								)}
							>
								{lessonTypes.map(t => (
									<MenuItem key={t.id} value={t.id}>
										{t.name}
									</MenuItem>
								))}
							</Select>
						</FormControl>

						<Divider>Формат проведення</Divider>
						
						<RadioGroup
							row
							value={formData.online ? 'online' : 'offline'}
							onChange={(e) => {
								const isOnline = e.target.value === 'online'
								setFormData({ 
									...formData, 
									online: isOnline,
									buildingId: isOnline ? undefined : formData.buildingId,
									earmarkId: isOnline ? undefined : formData.earmarkId,
									auditoriumId: isOnline ? undefined : formData.auditoriumId
								})
							}}
						>
							<FormControlLabel value="offline" control={<Radio />} label="Очно" />
							<FormControlLabel value="online" control={<Radio />} label="Онлайн" />
						</RadioGroup>

						{!formData.online ? (
							<>
								{/* Building */}
								<FormControl fullWidth size='small'>
									<InputLabel>1. Корпус</InputLabel>
									<Select
										value={formData.buildingId || ''}
										label='1. Корпус'
										onChange={e =>
											setFormData({
												...formData,
												buildingId: Number(e.target.value),
												earmarkId: undefined,
												auditoriumId: undefined 
											})
										}
									>
										{buildings.map(b => (
											<MenuItem key={b.id} value={b.id}>
												{b.name}
											</MenuItem>
										))}
									</Select>
								</FormControl>

								{/* Earmark */}
								<FormControl fullWidth size='small' disabled={!formData.buildingId}>
									<InputLabel>2. Тип аудиторії (Earmark)</InputLabel>
									<Select
										value={formData.earmarkId || ''}
										label='2. Тип аудиторії (Earmark)'
										onChange={e =>
											setFormData({
												...formData,
												earmarkId: Number(e.target.value),
												auditoriumId: undefined
											})
										}
									>
										{filteredEarmarks.map(e => (
											<MenuItem key={e.id} value={e.id}>
												{e.name}
											</MenuItem>
										))}
									</Select>
								</FormControl>

								{/* Specific Auditorium */}
								<FormControl fullWidth size='small' disabled={!formData.earmarkId}>
									<InputLabel>3. Конкретна аудиторія (Cabinet)</InputLabel>
									<Select
										value={formData.auditoriumId || ''}
										label="3. Конкретна аудиторія (Cabinet)"
										onChange={e =>
											setFormData({
												...formData,
												auditoriumId: e.target.value ? Number(e.target.value) : undefined,
											})
										}
									>
										<MenuItem value=""><em>Автоматичний вибір</em></MenuItem>
										{filteredAuditoriums.map(a => (
											<MenuItem key={a.id} value={a.id}>
												{a.name}
											</MenuItem>
										))}
									</Select>
								</FormControl>

								<FormControlLabel
									control={
										<Checkbox
											checked={formData.allowMultipleAuditoriums || false}
											onChange={(e) =>
												setFormData({
													...formData,
													allowMultipleAuditoriums: e.target.checked,
												})
											}
										/>
									}
									label='Дозволити декілька аудиторій (якщо не вистачає місця)'
								/>
							</>
						) : (
							<TextField
								label="Посилання або метод підключення"
								fullWidth
								size="small"
								placeholder="Zoom, Google Meet, Link..."
								value={formData.onlineLink || ''}
								onChange={(e) => setFormData({ ...formData, onlineLink: e.target.value })}
							/>
						)}

						<Divider sx={{ my: 1 }} />

						{/* Teachers (Multi-select) */}
						<FormControl fullWidth size='small'>
							<InputLabel>Викладачі</InputLabel>
							<Select
								multiple
								value={(formData.teacherIds || []) as any}
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
								input={<OutlinedInput label='Викладачі' />}
								renderValue={(selected: any[]) => (
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
							<InputLabel>Групи</InputLabel>
							<Select
								multiple
								value={(formData.groupIds || []) as any}
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
								input={<OutlinedInput label='Групи' />}
								renderValue={(selected: any[]) => (
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

						<Divider sx={{ my: 1 }}>Обсяг та період</Divider>

						<Box sx={{ display: 'flex', gap: 2 }}>
							<TextField
								label='Всього годин'
								type='number'
								fullWidth
								size='small'
								value={formData.totalHours || ''}
								onChange={(e) =>
									setFormData({ ...formData, totalHours: parseInt(e.target.value) })
								}
							/>
						</Box>

						<Box sx={{ display: 'flex', gap: 2 }}>
							<TextField
								label='Дата початку'
								type='date'
								fullWidth
								size='small'
								InputLabelProps={{ shrink: true }}
								value={formData.startDate || ''}
								onChange={(e) => setFormData({ ...formData, startDate: e.target.value })}
							/>
							<TextField
								label='Дата закінчення'
								type='date'
								fullWidth
								size='small'
								InputLabelProps={{ shrink: true }}
								value={formData.endDate || ''}
								onChange={(e) => setFormData({ ...formData, endDate: e.target.value })}
							/>
						</Box>
					</Box>
				</DialogContent>
				<DialogActions>
					<Button onClick={() => setOpenDialog(false)}>Скасувати</Button>
					<Button onClick={handleSubmit}>Зберегти</Button>
				</DialogActions>
			</Dialog>
		</Grid>
	)
}
