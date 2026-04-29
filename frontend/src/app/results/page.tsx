'use client'

import ScheduleView from '@/components/views/ScheduleView'
import ScheduleWizard from '@/components/views/ScheduleWizard'
import InstallPWA from '@/components/InstallPWA'
import { AppBar, Box, Button, Toolbar, Typography } from '@mui/material'
import Link from 'next/link'
import ArrowBackIcon from '@mui/icons-material/ArrowBack'
import LoginIcon from '@mui/icons-material/Login'
import SettingsIcon from '@mui/icons-material/Settings'
import { useAuth } from '@/app/providers'
import { useEffect, useState } from 'react'

export default function ResultsPage() {
	const { isAuthenticated } = useAuth()
	const [userPrefs, setUserPrefs] = useState<{ role: 'student' | 'teacher'; id: number } | null>(null)
	const [isLoaded, setIsLoaded] = useState(false)

	useEffect(() => {
		const role = localStorage.getItem('userRole') as 'student' | 'teacher' | null
		const id = localStorage.getItem('userId')
		if (role && id) {
			setUserPrefs({ role, id: Number(id) })
		}
		setIsLoaded(true)
	}, [])

	const handleWizardComplete = (role: 'student' | 'teacher', id: number) => {
		localStorage.setItem('userRole', role)
		localStorage.setItem('userId', id.toString())
		setUserPrefs({ role, id })
	}

	const handleResetPrefs = () => {
		localStorage.removeItem('userRole')
		localStorage.removeItem('userId')
		setUserPrefs(null)
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
							startIcon={<SettingsIcon />} 
							onClick={handleResetPrefs}
							size="small"
						>
							Змінити дані
						</Button>
					)}
				</Toolbar>
			</AppBar>

			<Box sx={{ flexGrow: 1, overflow: userPrefs ? 'hidden' : 'auto', display: 'flex', flexDirection: 'column' }}>
				{userPrefs ? (
					<Box sx={{ flexGrow: 1, p: { xs: 1, md: 3 }, overflow: 'hidden', display: 'flex' }}>
						<ScheduleView 
							initialMode={userPrefs.role === 'student' ? 'group' : 'teacher'} 
							initialId={userPrefs.id} 
						/>
					</Box>
				) : (
					<ScheduleWizard onComplete={handleWizardComplete} />
				)}
			</Box>
		</Box>
	)
}
