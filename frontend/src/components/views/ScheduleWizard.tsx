'use client'

import {
	Box,
	Button,
	Card,
	CardActionArea,
	CardContent,
	CircularProgress,
	Container,
	FormControl,
	InputLabel,
	MenuItem,
	Select,
	Typography,
	Stepper,
	Step,
	StepLabel,
	Paper,
	Grid,
} from '@mui/material'
import {
	School as StudentIcon,
	Person as TeacherIcon,
	ChevronRight as NextIcon,
	RestartAlt as ResetIcon,
} from '@mui/icons-material'
import { useQuery } from '@tanstack/react-query'
import { useState } from 'react'
import {
	fetchFaculties,
	fetchSpecialities,
	fetchChairs,
	fetchGroups,
	fetchTeachers,
	Faculty,
	Speciality,
	Chair,
	Group,
	Teacher,
} from '@/lib/api/scheduleApi'

interface WizardData {
	role: 'student' | 'teacher' | null
	facultyId: number | null
	specialityId: number | null
	chairId: number | null
	year: number | null
	targetId: number | null // groupId or teacherId
}

export default function ScheduleWizard({ onComplete }: { onComplete: (role: 'student' | 'teacher', id: number) => void }) {
	const [step, setStep] = useState(0)
	const [data, setData] = useState<WizardData>({
		role: null,
		facultyId: null,
		specialityId: null,
		chairId: null,
		year: null,
		targetId: null,
	})

	const { data: faculties = [] } = useQuery({ queryKey: ['faculties'], queryFn: fetchFaculties })
	const { data: specialities = [] } = useQuery({ 
		queryKey: ['specialities', data.facultyId], 
		queryFn: fetchSpecialities,
		enabled: !!data.facultyId && data.role === 'student'
	})
	const { data: chairs = [] } = useQuery({ 
		queryKey: ['chairs', data.facultyId], 
		queryFn: fetchChairs,
		enabled: !!data.facultyId && data.role === 'teacher'
	})
	const { data: allGroups = [] } = useQuery({ queryKey: ['groups'], queryFn: fetchGroups, enabled: step >= 2 })
	const { data: allTeachers = [] } = useQuery({ queryKey: ['teachers'], queryFn: fetchTeachers, enabled: step >= 2 })

	const handleRoleSelect = (role: 'student' | 'teacher') => {
		setData({ ...data, role })
		setStep(1)
	}

	const filteredSpecialities = specialities.filter(s => s.facultyId === data.facultyId)
	const filteredChairs = chairs.filter(c => c.facultyId === data.facultyId)
	const filteredGroups = allGroups.filter(g => g.departmentId === data.specialityId && g.year === data.year)
	const filteredTeachers = allTeachers.filter(t => t.departmentId === data.chairId)

	const steps = ['Хто ви?', 'Факультет', data.role === 'student' ? 'Спеціальність та курс' : 'Кафедра', data.role === 'student' ? 'Група' : 'Викладач']

	const renderStep = () => {
		switch (step) {
			case 0:
				return (
					<Box sx={{ mt: 8, textAlign: 'center' }}>
						<Typography variant="h3" gutterBottom sx={{ fontWeight: 'bold', mb: 6 }}>
							Вітаємо у KNU Schedule
						</Typography>
						<Typography variant="h6" color="text.secondary" sx={{ mb: 8 }}>
							Для початку роботи, будь ласка, оберіть свою роль
						</Typography>
						<Grid container spacing={4} sx={{ justifyContent: 'center' }}>
							<Grid size={{ xs: 12, sm: 5 }}>
								<Card elevation={4} sx={{ transition: '0.3s', '&:hover': { transform: 'translateY(-5px)', boxShadow: 8 } }}>
									<CardActionArea onClick={() => handleRoleSelect('student')} sx={{ p: 4 }}>
										<StudentIcon sx={{ fontSize: 80, color: 'primary.main', mb: 2 }} />
										<Typography variant="h5">Я Студент</Typography>
									</CardActionArea>
								</Card>
							</Grid>
							<Grid size={{ xs: 12, sm: 5 }}>
								<Card elevation={4} sx={{ transition: '0.3s', '&:hover': { transform: 'translateY(-5px)', boxShadow: 8 } }}>
									<CardActionArea onClick={() => handleRoleSelect('teacher')} sx={{ p: 4 }}>
										<TeacherIcon sx={{ fontSize: 80, color: 'secondary.main', mb: 2 }} />
										<Typography variant="h5">Я Викладач</Typography>
									</CardActionArea>
								</Card>
							</Grid>
						</Grid>
					</Box>
				)

			case 1: // Faculty
				return (
					<Box sx={{ mt: 4 }}>
						<Typography variant="h5" gutterBottom sx={{ mb: 4 }}>Оберіть ваш факультет</Typography>
						<Grid container spacing={2}>
							{faculties.map(f => (
								<Grid size={12} key={f.id}>
									<Button 
										fullWidth 
										variant="outlined" 
										size="large"
										onClick={() => {
											setData({ ...data, facultyId: f.id })
											setStep(2)
										}}
										sx={{ justifyContent: 'space-between', textAlign: 'left', py: 2 }}
									>
										{f.name}
										<NextIcon />
									</Button>
								</Grid>
							))}
						</Grid>
					</Box>
				)

			case 2: // Speciality + Year OR Chair
				if (data.role === 'student') {
					return (
						<Box sx={{ mt: 4, display: 'flex', flexDirection: 'column', gap: 3 }}>
							<Typography variant="h5" gutterBottom>Оберіть спеціальність та курс</Typography>
							<FormControl fullWidth>
								<InputLabel>Спеціальність</InputLabel>
								<Select 
									value={data.specialityId || ''} 
									label="Спеціальність"
									onChange={(e) => setData({ ...data, specialityId: Number(e.target.value) })}
								>
									{filteredSpecialities.map(s => <MenuItem key={s.id} value={s.id}>{s.name}</MenuItem>)}
								</Select>
							</FormControl>
							<FormControl fullWidth disabled={!data.specialityId}>
								<InputLabel>Курс</InputLabel>
								<Select 
									value={data.year || ''} 
									label="Курс"
									onChange={(e) => setData({ ...data, year: Number(e.target.value) })}
								>
									{[1,2,3,4,5,6].map(y => <MenuItem key={y} value={y}>{y} курс</MenuItem>)}
								</Select>
							</FormControl>
							<Button 
								variant="contained" 
								disabled={!data.specialityId || !data.year}
								onClick={() => setStep(3)}
								sx={{ mt: 2, py: 1.5 }}
							>
								Далі
							</Button>
						</Box>
					)
				} else {
					return (
						<Box sx={{ mt: 4 }}>
							<Typography variant="h5" gutterBottom sx={{ mb: 4 }}>Оберіть кафедру</Typography>
							<Grid container spacing={2}>
								{filteredChairs.map(c => (
									<Grid size={12} key={c.id}>
										<Button 
											fullWidth 
											variant="outlined" 
											size="large"
											onClick={() => {
												setData({ ...data, chairId: c.id })
												setStep(3)
											}}
											sx={{ justifyContent: 'space-between', textAlign: 'left', py: 2 }}
										>
											{c.name}
											<NextIcon />
										</Button>
									</Grid>
								))}
							</Grid>
						</Box>
					)
				}

			case 3: // Group OR Teacher
				const options = data.role === 'student' ? filteredGroups : filteredTeachers
				return (
					<Box sx={{ mt: 4 }}>
						<Typography variant="h5" gutterBottom sx={{ mb: 4 }}>
							Останній крок: оберіть {data.role === 'student' ? 'групу' : 'себе у списку'}
						</Typography>
						<Grid container spacing={2}>
							{options.map((item: any) => (
								<Grid size={{ xs: 12, sm: 6 }} key={item.id}>
									<Button 
										fullWidth 
										variant="contained" 
										color="primary"
										size="large"
										onClick={() => onComplete(data.role!, item.id)}
										sx={{ py: 2 }}
									>
										{item.name}
									</Button>
								</Grid>
							))}
						</Grid>
					</Box>
				)
		}
	}

	return (
		<Container maxWidth="md" sx={{ py: 6 }}>
			{step > 0 && (
				<Box sx={{ mb: 6 }}>
					<Stepper activeStep={step}>
						{steps.map((label) => (
							<Step key={label}>
								<StepLabel>{label}</StepLabel>
							</Step>
						))}
					</Stepper>
					<Button 
						startIcon={<ResetIcon />} 
						onClick={() => setStep(0)} 
						sx={{ mt: 2 }}
						size="small"
					>
						Почати спочатку
					</Button>
				</Box>
			)}
			<Paper elevation={0} sx={{ p: 4, bgcolor: 'transparent' }}>
				{renderStep()}
			</Paper>
		</Container>
	)
}
