import type { Metadata, Viewport } from 'next'
import Providers from './providers'
import PWARegistration from './pwa-registration'

export const metadata: Metadata = {
	title: 'KNU Schedule',
	description: 'Automated Scheduling System',
	manifest: '/manifest.json',
	appleWebApp: {
		capable: true,
		statusBarStyle: 'default',
		title: 'KNU Schedule',
	},
}

export const viewport: Viewport = {
	themeColor: '#1976d2',
}

export default function RootLayout({
	children,
}: Readonly<{
	children: React.ReactNode
}>) {
	return (
		<html lang='uk'>
			<head>
				<link rel="icon" href="/favicon.ico" sizes="any" />
				<link rel="apple-touch-icon" href="/window.svg" />
			</head>
			<body>
				<Providers>
					<PWARegistration />
					{children}
				</Providers>
			</body>
		</html>
	)
}
