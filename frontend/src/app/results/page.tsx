'use client'

import ScheduleView from '@/components/views/ScheduleView'
import ScheduleWizard from '@/components/views/ScheduleWizard'
import InstallPWA from '@/components/InstallPWA'
import { 
	AppBar, 
	Box, 
	Button, 
	Toolbar, 
	Typography, 
	Dialog, 
	DialogTitle, 
	DialogContent, 
	DialogActions,
	Divider,
	Autocomplete,
	TextField,
	ToggleButtonGroup,
	ToggleButton
} from '@mui/material'
import Link from 'next/link'
import ArrowBackIcon from '@mui/icons-material/ArrowBack'
import LoginIcon from '@mui/icons-material/Login'
import AccountCircleIcon from '@mui/icons-material/AccountCircle'
import LogoutIcon from '@mui/icons-material/Logout'
import { useAuth } from '@/app/providers'
import { useEffect, useState, useMemo } from 'react'
import { 
	fetchTeachers, 
	fetchGroups, 
	fetchFaculties, 
	fetchChairs, 
	fetchSpecialities 
} from '@/lib/api/scheduleApi'
import { useQuery } from '@tanstack/react-query'

export default function ResultsPage() {
	const { isAuthenticated } = useAuth()
	const [userPrefs, setUserPrefs] = useState<{ role: 'student' | 'teacher'; id: number } | null>(null)
	const [isLoaded, setIsLoaded] = useState(false)
	const [isProfileOpen, setIsProfileOpen] = useState(false)

	// Profile editing state
	const [profileRole, setProfileRole] = useState<'student' | 'teacher'>('student')
	const [selectedFacultyId, setSelectedFacultyId] = useState<number | null>(null)
	const [selectedChairId, setSelectedChairId] = useState<number | null>(null)
	const [selectedSpecialityId, setSelectedSpecialityId] = useState<number | null>(null)
	const [tempSelectedId, setTempSelectedId] = useState<number | null>(null)

	const { data: faculties = [] } = useQuery({ queryKey: ['faculties'], queryFn: fetchFaculties })
	const { data: chairs = [] } = useQuery({ queryKey: ['chairs'], queryFn: fetchChairs })
	const { data: specialities = [] } = useQuery({ queryKey: ['specialities'], queryFn: fetchSpecialities })
	const { data: teachers = [] } = useQuery({ queryKey: ['teachers'], queryFn: fetchTeachers })
	const { data: groups = [] } = useQuery({ queryKey: ['groups'], queryFn: fetchGroups })

	useEffect(() => {
		const role = localStorage.getItem('userRole') as 'student' | 'teacher' | null
		const id = localStorage.getItem('userId')
		if (role && id) {
			const parsedId = Number(id)
			setUserPrefs({ role, id: parsedId })
			
			// Initialize profile edit state
			setProfileRole(role)
			setTempSelectedId(parsedId)
			
			if (role === 'teacher') {
				const teacher = teachers.find(t => t.id === parsedId)
				if (teacher) {
					const chair = chairs.find(c => c.id === teacher.departmentId)
					if (chair) {
						setSelectedChairId(chair.id)
						setSelectedFacultyId(chair.facultyId)
					}
				}
			} else {
				const group = groups.find(g => g.id === parsedId)
				if (group) {
					const spec = specialities.find(s => s.id === group.departmentId)
					if (spec) {
						setSelectedSpecialityId(spec.id)
						setSelectedFacultyId(spec.facultyId)
					}
				}
			}
		}
		setIsLoaded(true)
	}, [isLoaded, teachers, groups, chairs, specialities])

	const handleWizardComplete = (role: 'student' | 'teacher', id: number) => {
		localStorage.setItem('userRole', role)
		localStorage.setItem('userId', id.toString())
		setUserPrefs({ role, id })
	}

	const handleResetPrefs = () => {
		localStorage.removeItem('userRole')
		localStorage.removeItem('userId')
		setUserPrefs(null)
		setIsProfileOpen(false)
	}

	const handleSaveProfile = () => {
		if (tempSelectedId) {
			localStorage.setItem('userRole', profileRole)
			localStorage.setItem('userId', tempSelectedId.toString())
			setUserPrefs({ role: profileRole, id: tempSelectedId })
			setIsProfileOpen(false)
		}
	}

	if (!isLoaded) return null

	return (
		<Box sx={{ flexGrow: 1, height: '100vh', display: 'flex', flexDirection: 'column', bgcolor: userPrefs ? '#f5f5f5' : 'background.default' }}>
			<InstallPWA />
			<AppBar position='static' color={userPrefs ? 'primary' : 'default'} elevation={userPrefs ? 1 : 0}>
				<Toolbar variant='dense'>
					{isAuthenticated ? (
						<Button
							component={Link}
							href="/"
							startIcon={<ArrowBackIcon />}
							color="inherit"
							sx={{ mr: 2 }}
						>
							Управління
						</Button>
					) : (
						<Button
							component={Link}
							href="/login"
							startIcon={<LoginIcon />}
							color="inherit"
							sx={{ mr: 2 }}
						>
							Вхід
						</Button>
					)}
					<Typography variant='h6' color='inherit' component='div' sx={{ flexGrow: 1 }}>
						{userPrefs ? 'Ваш розклад' : 'KNU Schedule'}
					</Typography>
					
					{userPrefs && (
						<Button 
							color="inherit" 
							startIcon={<AccountCircleIcon />} 
							onClick={() => setIsProfileOpen(true)}
							size="small"
						>
							Профіль
						</Button>
					)}
				</Toolbar>
			</AppBar>

			<Box sx={{ flexGrow: 1, overflow: userPrefs ? 'hidden' : 'auto', display: 'flex', flexDirection: 'column' }}>
				{userPrefs ? (
					<Box sx={{ flexGrow: 1, p: { xs: 1, md: 3 }, overflow: 'hidden', display: 'flex' }}>
						<ScheduleView 
							key={`${userPrefs.role}-${userPrefs.id}`}
							initialMode={userPrefs.role === 'student' ? 'group' : 'teacher'} 
							initialId={userPrefs.id} 
						/>
					</Box>
				) : (
					<ScheduleWizard onComplete={handleWizardComplete} />
				)}
			</Box>

			{/* Profile Dialog */}
			<Dialog open={isProfileOpen} onClose={() => setIsProfileOpen(false)} maxWidth="xs" fullWidth>
				<DialogTitle sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
					<AccountCircleIcon color="primary" /> Профіль користувача
				</DialogTitle>
				<DialogContent dividers>
					<Box sx={{ display: 'flex', flexDirection: 'column', gap: 2, py: 1 }}>
						<Box>
							<Typography variant="subtitle2" gutterBottom>Тип профілю</Typography>
							<ToggleButtonGroup
								value={profileRole}
								exclusive
								onChange={(e, next) => {
									if (next) {
										setProfileRole(next)
										setSelectedFacultyId(null)
										setSelectedChairId(null)
										setSelectedSpecialityId(null)
										setTempSelectedId(null)
									}
								}}
								size="small"
								fullWidth
							>
								<ToggleButton value="student">Студент</ToggleButton>
								<ToggleButton value="teacher">Викладач</ToggleButton>
							</ToggleButtonGroup>
						</Box>

						<Autocomplete
							options={faculties}
							getOptionLabel={option => option.name}
							renderInput={params => <TextField {...params} label="Факультет" size="small" />}
							value={faculties.find(f => f.id === selectedFacultyId) || null}
							onChange={(e, value) => {
								setSelectedFacultyId(value?.id || null)
								setSelectedChairId(null)
								setSelectedSpecialityId(null)
								setTempSelectedId(null)
							}}
						/>

						{profileRole === 'teacher' ? (
							<>
								<Autocomplete
									options={chairs.filter(c => !selectedFacultyId || c.facultyId === selectedFacultyId)}
									getOptionLabel={option => option.name}
									renderInput={params => <TextField {...params} label="Кафедра" size="small" />}
									value={chairs.find(c => c.id === selectedChairId) || null}
									onChange={(e, value) => {
										setSelectedChairId(value?.id || null)
										setTempSelectedId(null)
									}}
									disabled={!selectedFacultyId}
								/>
								<Autocomplete
									options={teachers.filter(t => !selectedChairId || t.departmentId === selectedChairId)}
									getOptionLabel={option => option.name}
									renderInput={params => <TextField {...params} label="Викладач" size="small" />}
									value={teachers.find(t => t.id === tempSelectedId) || null}
									onChange={(e, value) => setTempSelectedId(value?.id || null)}
									disabled={!selectedChairId}
								/>
							</>
						) : (
							<>
								<Autocomplete
									options={specialities.filter(s => !selectedFacultyId || s.facultyId === selectedFacultyId)}
									getOptionLabel={option => option.name}
									renderInput={params => <TextField {...params} label="Спеціальність" size="small" />}
									value={specialities.find(s => s.id === selectedSpecialityId) || null}
									onChange={(e, value) => {
										setSelectedSpecialityId(value?.id || null)
										setTempSelectedId(null)
									}}
									disabled={!selectedFacultyId}
								/>
								<Autocomplete
									options={groups.filter(g => !selectedSpecialityId || g.departmentId === selectedSpecialityId)}
									getOptionLabel={option => option.name}
									renderInput={params => <TextField {...params} label="Група" size="small" />}
									value={groups.find(g => g.id === tempSelectedId) || null}
									onChange={(e, value) => setTempSelectedId(value?.id || null)}
									disabled={!selectedSpecialityId}
								/>
							</>
						)}
					</Box>
				</DialogContent>
				<DialogActions sx={{ p: 2 }}>
					<Button 
						color="error" 
						variant="outlined"
						startIcon={<LogoutIcon />} 
						onClick={handleResetPrefs}
						size="small"
					>
						Скинути
					</Button>
					<Box sx={{ flexGrow: 1 }} />
					<Button onClick={() => setIsProfileOpen(false)} size="small">Скасувати</Button>
					<Button 
						variant="contained" 
						onClick={handleSaveProfile} 
						disabled={!tempSelectedId}
						size="small"
					>
						Зберегти
					</Button>
				</DialogActions>
			</Dialog>
		</Box>
	)
}
