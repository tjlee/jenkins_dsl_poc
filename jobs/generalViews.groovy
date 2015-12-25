import com.setdsl.ListViewsTemplateBuilder

new ListViewsTemplateBuilder(
        name: 'generate_views',
        description: '',
        views: ['flex': 'build_flex.*',
                'DEV': '(build_e.*)|(build_i.*)|(build_t.*)',
                'LENTA': '(build_lenta_.*)',
                'BELARUS': '(build_belarus_.*)',
                'PULL_REQUESTS': '(build_pull.*)',
                'seed': '(.*seed)'] as Map<String, String>

).build(this)

