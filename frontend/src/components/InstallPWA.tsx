'use client'

import { useState, useEffect } from 'react'
import { Button } from '@mui/material'
import InstallMobileIcon from '@mui/icons-material/InstallMobile'

export default function InstallPWA() {
	const [deferredPrompt, setDeferredPrompt] = useState<any>(null)
	const [isVisible, setIsVisible] = useState(false)

	useEffect(() => {
		const handler = (e: any) => {
			// Prevent Chrome 67 and earlier from automatically showing the prompt
			e.preventDefault()
			// Stash the event so it can be triggered later.
			setDeferredPrompt(e)
			setIsVisible(true)
		}

		window.addEventListener('beforeinstallprompt', handler)

		return () => {
			window.removeEventListener('beforeinstallprompt', handler)
		}
	}, [])

	const handleInstallClick = async () => {
		if (!deferredPrompt) return

		// Show the prompt
		deferredPrompt.prompt()
		// Wait for the user to respond to the prompt
		const { outcome } = await deferredPrompt.userChoice
		console.log(`User response to the install prompt: ${outcome}`)
		// We've used the prompt, and can't use it again, throw it away
		setDeferredPrompt(null)
		setIsVisible(false)
	}

	if (!isVisible) return null

	return (
		<Button
			variant="contained"
			color="secondary"
			size="small"
			startIcon={<InstallMobileIcon />}
			onClick={handleInstallClick}
			sx={{ 
				position: 'fixed', 
				bottom: 16, 
				right: 16, 
				zIndex: 1000,
				borderRadius: '20px',
				boxShadow: 3
			}}
		>
			Встановити додаток
		</Button>
	)
}
