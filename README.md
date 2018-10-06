# jenkins-library
Shared Library for Jenkins Pipeline

# How to use ?
- See [Using libraries](https://jenkins.io/doc/book/pipeline/shared-libraries/#using-libraries) to get more information.

## Set your customize configures
1. Customize configures will be replace the same variables in file `config.groovy`

2. Create a file in dir **/path/to/my/files** and named **my-settings.groovy**
  - /path/to/my/files/my-settings.groovy

3. Write your Key and Value like the following lines

        // First var
        KEY_NAME    = 'value-1'

        KEY_NUMBER  = 123

        CI_NAME     = JOB_NAME

4. Configure **Environment variables** in:
  - -> Manage Jenkins
    - -> Configure System
      - -> Global properties
        - Create new one
          - Name:  `SETTINGS`
          - Value: `/path/to/my/files/my-settings.groovy`