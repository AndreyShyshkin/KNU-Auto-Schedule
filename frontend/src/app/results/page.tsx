'use client'

import ScheduleView from '@/components/views/ScheduleView'
import { AppBar, Box, Button, Toolbar, Typography, Paper } from '@mui/material'
import Link from 'next/link'
import ArrowBackIcon from '@mui/icons-material/ArrowBack'

export default function ResultsPage() {
	return (
		<Box sx={{ flexGrow: 1, height: '100vh', display: 'flex', flexDirection: 'column' }}>
			<AppBar position='static' color='primary' elevation={1}>
				<Toolbar variant='dense'>
					<Button
						component={Link}
						href="/"
						startIcon={<ArrowBackIcon />}
						color="inherit"
						sx={{ mr: 2 }}
					>
						Назад до управління
					</Button>
					<Typography variant='h6' color='inherit' component='div' sx={{ flexGrow: 1 }}>
						Повний розклад
					</Typography>
				</Toolbar>
			</AppBar>

			<Box sx={{ flexGrow: 1, p: 3, overflow: 'hidden', bgcolor: '#f5f5f5' }}>
				<Paper sx={{ p: 2, height: '100%', display: 'flex', flexDirection: 'column', borderRadius: 2 }}>
					<ScheduleView />
				</Paper>
			</Box>
		</Box>
	)
}
