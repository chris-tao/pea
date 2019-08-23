import { NgModule } from '@angular/core'
import { RouterModule, Routes } from '@angular/router'

const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: '/jobs' },
  { path: 'jobs', loadChildren: () => import('./pages/running-jobs/running-jobs.module').then(m => m.RunningJobsModule) },
  { path: 'peas', loadChildren: () => import('./pages/worker-peas/worker-peas.module').then(m => m.WorkerPeasModule) },
  { path: 'shoot', loadChildren: () => import('./pages/lets-shoot/lets-shoot.module').then(m => m.LetsShootModule) },
  { path: 'reports', loadChildren: () => import('./pages/local-reports/local-reports.module').then(m => m.LocalReportsModule) },
  { path: '**', redirectTo: '' },
]

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
