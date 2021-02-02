# jenkins-library
Shared Library for Jenkins Pipeline

- 关于Shared Library的更多信息请参考[Using libraries](https://jenkins.io/doc/book/pipeline/shared-libraries/#using-libraries) 

## 配置 Jenkins 使用 Shared Library
若要使用 Shared Library, 您可以通过两种方式进行配置:
- Declarative Pipeline
  通过简单配置使用 `Jenkinsfile` 进行控制

  Jenkinsfile 在执行时, 也是调用了 **Jenkins Shared Library**

- Scripted Pipeline
  通过一些配置使用 `Script` 进行控制

### Declarative Pipeline
#### 1. 创建一个流水线(Pipeline)项目
创建项目

#### 2. 配置使用 Jenkinsfile
在项目配置界面，**流水线** 处，选择 `Pipeline script from SCM`
  - SCM
    - Git
      - Repository URL
      - https://github.com/Statemood/jenkins-library
      - Branch Specifier (blank for 'any')
      - origin/master
    - 脚本路径
      - Jenkinsfile

或选择 `Pipeline script`, 然后输入如下配置

```groovy
@Library('github.com/Statemood/jenkins-library@dev') _

entry([git_repo: 'https://github.com/Statemood/simple-java-maven-app.git'])
```

保存即可开始使用。


#### 3. 配置 Global Pipeline Libraries
- 配置方式参考 https://jenkins.io/doc/book/pipeline/shared-libraries/#global-shared-libraries


## 依赖项
BUILD_USER & BUILD_USER_ID 需要 [build user vars](https://plugins.jenkins.io/build-user-vars-plugin) 插件