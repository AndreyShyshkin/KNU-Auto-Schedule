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
	fetchBuildings,
	fetchLessonTypes,
	fetchFaculties,
	fetchSpecialities,
	fetchChairs,
	ScheduleVersion,
	ScheduleEntryDto,
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
	IconButton,
	Tooltip,
	Slider,
	Stack,
	Drawer,
	Divider,
	Badge,
	InputAdornment,
} from '@mui/material'
import { useQuery } from '@tanstack/react-query'
import { useState, useEffect, useMemo } from 'react'
import FileDownloadIcon from '@mui/icons-material/FileDownload'
import PictureAsPdfIcon from '@mui/icons-material/PictureAsPdf'
import ChevronLeftIcon from '@mui/icons-material/ChevronLeft'
import ChevronRightIcon from '@mui/icons-material/ChevronRight'
import ZoomInIcon from '@mui/icons-material/ZoomIn'
import FilterListIcon from '@mui/icons-material/FilterList'
import CloseIcon from '@mui/icons-material/Close'
import SearchIcon from '@mui/icons-material/Search'
import ClearIcon from '@mui/icons-material/Clear'
import { Calendar, dateFnsLocalizer, Views, View } from 'react-big-calendar'
import { 
	format, 
	parse, 
	startOfWeek, 
	getDay, 
	addHours, 
	startOfDay, 
	endOfDay, 
	isWithinInterval,
	startOfMonth,
	endOfMonth,
	addDays
} from 'date-fns'
import { uk } from 'date-fns/locale/uk'
import 'react-big-calendar/lib/css/react-big-calendar.css'

const locales = {
	'uk': uk,
}

const localizer = dateFnsLocalizer({
	format,
	parse,
	startOfWeek,
	getDay,
	locales,
})

interface ScheduleViewProps {
	initialMode?: 'teacher' | 'group' | 'all'
	initialId?: number | null
}

export default function ScheduleView({ initialMode = 'all', initialId = null }: ScheduleViewProps) {
	const [mode, setMode] = useState<'teacher' | 'group' | 'all'>(initialMode)
	const [selectedId, setSelectedId] = useState<number | null>(initialId)
	const [selectedVersionId, setSelectedVersionId] = useState<number | ''>('')
	const [currentView, setCurrentView] = useState<View>(Views.WEEK)
	const [currentDate, setCurrentDate] = useState(new Date())
	const [zoom, setZoom] = useState(60)

	// Additional Filters
	const [filtersOpen, setFiltersOpen] = useState(false)
	const [searchQuery, setSearchQuery] = useState('')
	const [filterBuilding, setFilterBuilding] = useState<string>('')
	const [filterLessonType, setFilterLessonType] = useState<string>('')
	const [selectedFacultyId, setSelectedFacultyId] = useState<number | null>(null)
	const [selectedSpecialityId, setSelectedSpecialityId] = useState<number | null>(null)
	const [selectedChairId, setSelectedChairId] = useState<number | null>(null)

	const { data: versions = [] } = useQuery({
		queryKey: ['scheduleVersions'],
		queryFn: fetchScheduleVersions,
	})

	const { data: buildings = [] } = useQuery({
		queryKey: ['buildings'],
		queryFn: fetchBuildings,
	})

	const { data: lessonTypes = [] } = useQuery({
		queryKey: ['lessonTypes'],
		queryFn: fetchLessonTypes,
	})

	const { data: faculties = [] } = useQuery({
		queryKey: ['faculties'],
		queryFn: fetchFaculties,
	})

	const { data: specialities = [] } = useQuery({
		queryKey: ['specialities'],
		queryFn: fetchSpecialities,
	})

	const { data: chairs = [] } = useQuery({
		queryKey: ['chairs'],
		queryFn: fetchChairs,
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
	})
	const { data: groups = [] } = useQuery({
		queryKey: ['groups'],
		queryFn: fetchGroups,
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

	const filteredSchedule = useMemo(() => {
		return schedule.filter(entry => {
			const query = searchQuery.toLowerCase()
			const matchesSearch = !searchQuery || 
				entry.subjectName.toLowerCase().includes(query) ||
				entry.buildingName.toLowerCase().includes(query) ||
				entry.auditoriumName.toLowerCase().includes(query) ||
				entry.additionalInfo.toLowerCase().includes(query)
			
			const matchesBuilding = !filterBuilding || entry.buildingName === filterBuilding
			const matchesLessonType = !filterLessonType || entry.lessonTypeName.includes(filterLessonType)

			return matchesSearch && matchesBuilding && matchesLessonType
		})
	}, [schedule, searchQuery, filterBuilding, filterLessonType])

	const events = useMemo(() => {
		return filteredSchedule.map((entry, index) => {
			const datePart = entry.actualDate || format(new Date(), 'yyyy-MM-dd')
			const start = parse(`${datePart} ${entry.timeStart}`, 'yyyy-MM-dd HH:mm', new Date())
			const end = parse(`${datePart} ${entry.timeEnd}`, 'yyyy-MM-dd HH:mm', new Date())
			
			return {
				id: index,
				title: entry.subjectName,
				start,
				end,
				resource: entry,
			}
		})
	}, [filteredSchedule])

	const activeFiltersCount = useMemo(() => {
		let count = 0
		if (mode !== 'all') count++
		if (searchQuery) count++
		if (filterBuilding) count++
		if (filterLessonType) count++
		if (selectedFacultyId) count++
		if (selectedSpecialityId) count++
		if (selectedChairId) count++
		return count
	}, [mode, searchQuery, filterBuilding, filterLessonType, selectedFacultyId, selectedSpecialityId, selectedChairId])

	const resetFilters = () => {
		setMode('all')
		setSelectedId(null)
		setSearchQuery('')
		setFilterBuilding('')
		setFilterLessonType('')
		setSelectedFacultyId(null)
		setSelectedSpecialityId(null)
		setSelectedChairId(null)
	}

	const getVisibleRange = (date: Date, view: View) => {
		if (view === Views.MONTH) {
			return {
				start: startOfMonth(date),
				end: endOfMonth(date),
			}
		}
		if (view === Views.WEEK) {
			const start = startOfWeek(date, { weekStartsOn: 1 })
			const end = endOfDay(addDays(start, 6))
			return { start, end }
		}
		return {
			start: startOfDay(date),
			end: endOfDay(date),
		}
	}

	const getExportData = () => {
		const { start, end } = getVisibleRange(currentDate, currentView)
		return filteredSchedule.filter(entry => {
			if (!entry.actualDate) return false
			const date = parse(entry.actualDate, 'yyyy-MM-dd', new Date())
			return date >= start && date <= end
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

	const CustomEvent = ({ event }: any) => {
		const resource = event.resource as ScheduleEntryDto
		
		const renderAuditorium = (name: string) => {
			if (name?.includes('http')) {
				const linkMatch = name.match(/https?:\/\/[^\s]+/);
				if (linkMatch) {
					return (
						<a 
							href={linkMatch[0]} 
							target="_blank" 
							rel="noopener noreferrer"
							style={{ color: 'inherit', textDecoration: 'underline', fontWeight: 'bold' }}
							onClick={(e) => e.stopPropagation()}
						>
							{name.length > 30 ? 'Посилання на пару' : name}
						</a>
					)
				}
			}
			return name
		}

		return (
			<Tooltip title={`${resource.subjectName} (${resource.lessonTypeName})\n${resource.buildingName}, ${resource.auditoriumName}\n${resource.additionalInfo}`}>
				<Box sx={{ fontSize: '0.75rem', lineHeight: 1.2, overflow: 'hidden' }}>
					<Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', mb: 0.5 }}>
						<Typography variant="caption" sx={{ fontWeight: 'bold', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap', mr: 0.5 }}>
							{resource.subjectName}
						</Typography>
						{resource.lessonTypeName && (
							<Typography 
								variant="caption" 
								sx={{ 
									fontSize: '0.65rem', 
									bgcolor: 'rgba(0,0,0,0.08)', 
									px: 0.5, 
									borderRadius: 0.5,
									fontWeight: 'medium',
									whiteSpace: 'nowrap'
								}}
							>
								{resource.lessonTypeName}
							</Typography>
						)}
					</Box>
					<Typography variant="caption" sx={{ display: 'block', opacity: 0.9 }}>
						{renderAuditorium(resource.auditoriumName)} {resource.buildingName ? `(${resource.buildingName})` : ''}
					</Typography>
					{currentView !== Views.MONTH && (
						<Typography variant="caption" sx={{ display: 'block', fontStyle: 'italic', opacity: 0.8, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
							{resource.additionalInfo}
						</Typography>
					)}
				</Box>
			</Tooltip>
		)
	}

	return (
		<Box sx={{ display: 'flex', flexDirection: 'column', height: '100%', width: '100%', gap: 2 }}>
			{/* Header with Filters */}
			<Box sx={{ display: 'flex', gap: 2, alignItems: 'center', flexWrap: 'wrap', p: 1, bgcolor: 'background.paper', borderRadius: 2, boxShadow: 1 }}>
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
				
				<Button
					variant={activeFiltersCount > 0 ? "contained" : "outlined"}
					startIcon={<FilterListIcon />}
					onClick={() => setFiltersOpen(true)}
					size="small"
				>
					<Badge badgeContent={activeFiltersCount} color="error" sx={{ '& .MuiBadge-badge': { right: -10, top: 0 } }}>
						Фільтри
					</Badge>
				</Button>

				<Box sx={{ flexGrow: 1 }} />

				<Box sx={{ display: 'flex', gap: 1 }}>
					<Button
						variant="outlined"
						size="small"
						startIcon={<FileDownloadIcon />}
						onClick={handleExportExcel}
						disabled={events.length === 0}
					>
						Excel
					</Button>
					<Button
						variant="outlined"
						size="small"
						color="secondary"
						startIcon={<PictureAsPdfIcon />}
						onClick={handleExportPdf}
						disabled={events.length === 0}
					>
						PDF
					</Button>
				</Box>
			</Box>

			{/* Filter Drawer */}
			<Drawer
				anchor="right"
				open={filtersOpen}
				onClose={() => setFiltersOpen(false)}
			>
				<Box sx={{ width: 320, p: 3, display: 'flex', flexDirection: 'column', gap: 3 }}>
					<Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
						<Typography variant="h6">Фільтри</Typography>
						<IconButton onClick={() => setFiltersOpen(false)}>
							<CloseIcon />
						</IconButton>
					</Box>
					
					<Divider />

					<Box>
						<Typography variant="subtitle2" gutterBottom>Режим перегляду</Typography>
						<ToggleButtonGroup
							value={mode}
							exclusive
							onChange={(e, newMode) => {
								if (newMode) {
									setMode(newMode)
									setSelectedId(null)
									setSelectedFacultyId(null)
									setSelectedSpecialityId(null)
									setSelectedChairId(null)
								}
							}}
							size='small'
							fullWidth
						>
							<ToggleButton value='all'>Всі</ToggleButton>
							<ToggleButton value='teacher'>Викладач</ToggleButton>
							<ToggleButton value='group'>Група</ToggleButton>
						</ToggleButtonGroup>
					</Box>

					{mode === 'teacher' && (
						<>
							<Autocomplete
								options={faculties}
								getOptionLabel={option => option.name}
								renderInput={params => (
									<TextField {...params} label='Факультет' size='small' />
								)}
								onChange={(e, value) => {
									setSelectedFacultyId(value?.id || null)
									setSelectedChairId(null)
									setSelectedId(null)
								}}
								value={faculties.find(f => f.id === selectedFacultyId) || null}
							/>
							<Autocomplete
								options={chairs.filter(c => !selectedFacultyId || c.facultyId === selectedFacultyId)}
								getOptionLabel={option => option.name}
								renderInput={params => (
									<TextField {...params} label='Кафедра' size='small' />
								)}
								onChange={(e, value) => {
									setSelectedChairId(value?.id || null)
									setSelectedId(null)
								}}
								value={chairs.find(c => c.id === selectedChairId) || null}
								disabled={!selectedFacultyId}
							/>
							<Autocomplete
								options={teachers.filter(t => !selectedChairId || t.departmentId === selectedChairId)}
								getOptionLabel={option => option.name}
								renderInput={params => (
									<TextField {...params} label='Виберіть викладача' size='small' />
								)}
								onChange={(e, value) => setSelectedId(value?.id || null)}
								value={teachers.find(t => t.id === selectedId) || null}
								disabled={!selectedChairId}
							/>
						</>
					)}
					{mode === 'group' && (
						<>
							<Autocomplete
								options={faculties}
								getOptionLabel={option => option.name}
								renderInput={params => (
									<TextField {...params} label='Факультет' size='small' />
								)}
								onChange={(e, value) => {
									setSelectedFacultyId(value?.id || null)
									setSelectedSpecialityId(null)
									setSelectedId(null)
								}}
								value={faculties.find(f => f.id === selectedFacultyId) || null}
							/>
							<Autocomplete
								options={specialities.filter(s => !selectedFacultyId || s.facultyId === selectedFacultyId)}
								getOptionLabel={option => option.name}
								renderInput={params => (
									<TextField {...params} label='Спеціальність' size='small' />
								)}
								onChange={(e, value) => {
									setSelectedSpecialityId(value?.id || null)
									setSelectedId(null)
								}}
								value={specialities.find(s => s.id === selectedSpecialityId) || null}
								disabled={!selectedFacultyId}
							/>
							<Autocomplete
								options={groups.filter(g => !selectedSpecialityId || g.departmentId === selectedSpecialityId)}
								getOptionLabel={option => option.name}
								renderInput={params => (
									<TextField {...params} label='Виберіть групу' size='small' />
								)}
								onChange={(e, value) => setSelectedId(value?.id || null)}
								value={groups.find(g => g.id === selectedId) || null}
								disabled={!selectedSpecialityId}
							/>
						</>
					)}

					<Autocomplete
						freeSolo
						options={Array.from(new Set(schedule.map(entry => entry.subjectName)))}
						renderInput={(params) => (
							<TextField
								{...params}
								label="Пошук"
								size="small"
								placeholder="Предмет, викладач, аудиторія..."
								InputProps={{
									...params.InputProps,
									startAdornment: (
										<InputAdornment position="start">
											<SearchIcon fontSize="small" />
										</InputAdornment>
									),
									endAdornment: (
										<>
											{searchQuery && (
												<InputAdornment position="end">
													<IconButton size="small" onClick={() => setSearchQuery('')}>
														<ClearIcon fontSize="small" />
													</IconButton>
												</InputAdornment>
											)}
											{params.InputProps.endAdornment}
										</>
									)
								}}
							/>
						)}
						value={searchQuery}
						onInputChange={(e, value) => setSearchQuery(value)}
					/>

					<FormControl size='small' fullWidth>
						<InputLabel>Корпус</InputLabel>
						<Select
							value={filterBuilding}
							label='Корпус'
							onChange={(e) => setFilterBuilding(e.target.value)}
						>
							<MenuItem value="">Всі корпуси</MenuItem>
							{buildings.map((b) => (
								<MenuItem key={b.id} value={b.name}>{b.name}</MenuItem>
							))}
						</Select>
					</FormControl>

					<FormControl size='small' fullWidth>
						<InputLabel>Тип заняття</InputLabel>
						<Select
							value={filterLessonType}
							label='Тип заняття'
							onChange={(e) => setFilterLessonType(e.target.value)}
						>
							<MenuItem value="">Всі типи</MenuItem>
							{lessonTypes.map((t) => (
								<MenuItem key={t.id} value={t.name}>{t.name}</MenuItem>
							))}
						</Select>
					</FormControl>

					<Box sx={{ flexGrow: 1 }} />

					<Button 
						variant="outlined" 
						color="inherit" 
						onClick={resetFilters}
						fullWidth
					>
						Скинути фільтри
					</Button>
				</Box>
			</Drawer>

			{/* Calendar Controls */}
			<Box sx={{ display: 'flex', alignItems: 'center', gap: 3, px: 1 }}>
				<Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
					<IconButton size="small" onClick={() => {
						if (currentView === Views.MONTH) setCurrentDate(new Date(currentDate.getFullYear(), currentDate.getMonth() - 1, 1))
						else if (currentView === Views.WEEK) setCurrentDate(addDays(currentDate, -7))
						else if (currentView === Views.AGENDA) setCurrentDate(addDays(currentDate, -30))
						else setCurrentDate(addDays(currentDate, -1))
					}}>
						<ChevronLeftIcon />
					</IconButton>
					<Button size="small" variant="outlined" onClick={() => setCurrentDate(new Date())}>Сьогодні</Button>
					<IconButton size="small" onClick={() => {
						if (currentView === Views.MONTH) setCurrentDate(new Date(currentDate.getFullYear(), currentDate.getMonth() + 1, 1))
						else if (currentView === Views.WEEK) setCurrentDate(addDays(currentDate, 7))
						else if (currentView === Views.AGENDA) setCurrentDate(addDays(currentDate, 30))
						else setCurrentDate(addDays(currentDate, 1))
					}}>
						<ChevronRightIcon />
					</IconButton>
				</Box>

				<Typography variant="h6" sx={{ minWidth: 200, textAlign: 'center' }}>
					{currentView === Views.MONTH 
						? format(currentDate, 'LLLL yyyy', { locale: uk })
						: currentView === Views.WEEK
						? `${format(startOfWeek(currentDate, { weekStartsOn: 1 }), 'd MMM', { locale: uk })} - ${format(endOfDay(addDays(startOfWeek(currentDate, { weekStartsOn: 1 }), 6)), 'd MMM yyyy', { locale: uk })}`
						: currentView === Views.AGENDA
						? `${format(currentDate, 'd MMM', { locale: uk })} - ${format(addDays(currentDate, 30), 'd MMM yyyy', { locale: uk })}`
						: format(currentDate, 'd MMMM yyyy', { locale: uk })}
				</Typography>

				{(currentView === Views.WEEK || currentView === Views.DAY) && (
					<Stack direction="row" spacing={2} alignItems="center" sx={{ width: 150, ml: 2 }}>
						<Tooltip title="Масштаб">
							<ZoomInIcon color="action" fontSize="small" />
						</Tooltip>
						<Slider
							size="small"
							value={zoom}
							min={40}
							max={200}
							onChange={(e, newValue) => setZoom(newValue as number)}
						/>
					</Stack>
				)}

				<Box sx={{ flexGrow: 1 }} />

				<ToggleButtonGroup
					value={currentView}
					exclusive
					onChange={(e, nextView) => nextView && setCurrentView(nextView)}
					size='small'
				>
					<ToggleButton value={Views.DAY}>День</ToggleButton>
					<ToggleButton value={Views.WEEK}>Тиждень</ToggleButton>
					<ToggleButton value={Views.MONTH}>Місяць</ToggleButton>
					<ToggleButton value={Views.AGENDA}>Список</ToggleButton>
				</ToggleButtonGroup>
			</Box>

			{/* Calendar area */}
			<Box sx={{ 
				flexGrow: 1, 
				minHeight: 0, 
				width: '100%',
				position: 'relative',
				'& .rbc-calendar': {
					display: 'flex',
					flexDirection: 'column',
					height: '100%',
				},
				'& .rbc-time-view': {
					flex: '1 1 0%',
					display: 'flex',
					flexDirection: 'column',
					minHeight: 0,
				},
				'& .rbc-time-content': {
					flex: '1 1 0%',
					overflowY: 'auto !important',
					minHeight: 0,
				},
				'& .rbc-timeslot-group': {
					minHeight: `${zoom}px !important`,
				},
				'& .rbc-month-view': {
					flex: '1 1 0%',
					display: 'flex',
					flexDirection: 'column',
					minHeight: 0,
					overflow: 'hidden'
				},
				'& .rbc-month-row': {
					flex: 1,
					minHeight: 0
				},
				'& .rbc-agenda-view': {
					flex: '1 1 0%',
					display: 'flex',
					flexDirection: 'column',
					minHeight: 0,
					overflowY: 'auto'
				}
			}}>
				<Paper sx={{ 
					position: 'absolute',
					top: 0,
					left: 0,
					right: 0,
					bottom: 0,
					p: 2, 
					borderRadius: 2, 
					display: 'flex', 
					flexDirection: 'column',
					overflow: 'hidden'
				}}>
					<Calendar
						localizer={localizer}
						events={events}
						startAccessor="start"
						endAccessor="end"
						style={{ height: '100%' }}
						toolbar={false}
						messages={{
							next: "Наступний",
							previous: "Попередній",
							today: "Сьогодні",
							month: "Місяць",
							week: "Тиждень",
							day: "День",
							agenda: "Розклад",
							date: "Дата",
							time: "Час",
							event: "Подія",
						}}
						culture="uk"
						view={currentView}
						onView={(view) => setCurrentView(view)}
						date={currentDate}
						onNavigate={(date) => setCurrentDate(date)}
						onDrillDown={(date) => {
							setCurrentDate(date)
							setCurrentView(Views.DAY)
						}}
						components={{
							event: CustomEvent,
						}}
						min={new Date(0, 0, 0, 8, 0, 0)}
						max={new Date(0, 0, 0, 20, 0, 0)}
					/>
				</Paper>
			</Box>
		</Box>
	)
}
