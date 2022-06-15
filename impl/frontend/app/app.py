from flask import Flask, flash, render_template, send_from_directory, url_for, request, redirect
from flask_login import LoginManager, login_manager, current_user, login_user, login_required, logout_user
import os
# Usuarios
from models import users, User

# Login
from forms import LoginForm, RegisterForm, SendVideoForm
import requests
import json
import hashlib
import base64
import collections
app = Flask(__name__, static_url_path='')
login_manager = LoginManager()
login_manager.init_app(app)  # Para mantener la sesión

# Configurar el secret_key. OJO, no debe ir en un servidor git público.
# Python ofrece varias formas de almacenar esto de forma segura, que
# no cubriremos aquí.
app.config['SECRET_KEY'] = 'qH1vprMjavek52cv7Lmfe1FoCexrrV8egFnB21jHhkuOHm8hJUe1hwn7pKEZQ1fioUzDb3sWcNK1pJVVIhyrgvFiIrceXpKJBFIn_i9-LTLBCc4cqaI3gjJJHU6kxuT8bnC7Ng'

app.config["ALLOWED_VIDEO_EXTENSIONS"] = ["MP4", "AVI", "MKV"]
FILE_UPLOAD_MAX_MEMORY_SIZE = int(1024 * 1024 * 1024 * 1024)


def allowed_video(filename):
    if not "." in filename:
        return False
    ext = filename.rsplit(".", 1)[1]
    if ext.upper() in app.config["ALLOWED_VIDEO_EXTENSIONS"]:
        return True
    else:
        return False
    
    
@app.route('/static/<path:path>')
def serve_static(path):
    return send_from_directory('static', path)


@app.route('/')
def index():
    return render_template('index.html')


@app.route('/login', methods=['GET', 'POST'])
def login():
    if current_user.is_authenticated:
        return redirect(url_for('index'))
    else:
        error = None
        form = LoginForm(request.form)
        if request.method == "POST" and  form.validate():
            REST_SERVER = os.environ.get('REST_SERVER', 'localhost')
            credenciales = {"email":form.email.data, "password":form.password.data}
            response = requests.post("http://" + REST_SERVER + ":8080/rest/checkLogin", json=credenciales)
            # userName = form.email.data.encode('utf-8').split('@')[0]
            if (response.status_code == 200):
                # Tratar usuario devuelto en el response a través de json
                user = User.get_user(form.email.data.encode('utf-8'))
                user = User(int(response.json()['userid']['string']), response.json()['name'], form.email.data.encode('utf-8'), form.password.data.encode('utf-8'))  
                login_user(user, remember=form.remember_me.data)
                return redirect(url_for('profile'))
            else:
                error = response.status_code 
        return render_template('login.html', form=form, error=error)


@app.route('/register', methods=['GET', 'POST'])
def register():
    error = None
    REST_SERVER = os.environ.get('REST_SERVER', 'localhost')
    form = RegisterForm(request.form)
    if request.method == "POST" and form.validate_on_submit():
        credenciales = {"email":form.email.data, "name":form.username.data, "password":form.password.data}
        url = "http://" + REST_SERVER + ":8080/rest/register"
        #if request.method == 'POST' and 'username' in request.form and 'password' in request.form and 'email' in request.form:
        response = requests.post(url, json=credenciales)
        if(response.status_code == 201):
            user = User(int(response.json()['userid']['string']), form.username, form.email.data.encode('utf-8'),
                    form.password.data.encode('utf-8'))
            users.append(user)
            login_user(user)
            error = response.status_code
            return redirect(url_for('index'))
        elif (response.status_code == 409):
            error = response.status_code
            return render_template('register.html', form=form, error=error)
        elif (response.status_code == 200):
            error = response.status_code
            return render_template('register.html', form=form, error=error)
        else:
            error = response.status_code
            return render_template('register.html', form=form, error=error)

    return render_template('register.html', form=form, error=error)


@app.route('/videos/<idVideo>', methods=['GET', 'POST'])
@login_required
def video_info(idVideo):
    error = None
    REST_SERVER = os.environ.get('REST_SERVER', 'localhost')
    url = 'http://'+REST_SERVER+':8080/rest/users/'+str(current_user.id)+'/videos/'+idVideo
    response = requests.get(url)
    
    if response.status_code == 200: 
        data = response.json()
        jsonData = json.loads(response.text)
        return render_template('video_info.html', listFaces=jsonData, error=error)
    
    
    return render_template('list_videos.html', listFaces=data, error=error)




@app.route('/videos/', methods=['GET'])
@login_required
def list_videos():
    error = None
    if request.method == "GET":
        REST_SERVER = os.environ.get('REST_SERVER', 'localhost')
        url = 'http://'+REST_SERVER+':8080/rest/users/'+str(current_user.id)+'/videos/'
        response = requests.get(url)
        print('-------------------------------------------------------------------------')
        if response.status_code == 200: 
            data = response.json()

            return render_template('list_videos.html', data=data, error=error)
    error = response.status_code
    form = RegisterForm(request.form)
    return render_template('register.html', form = form, error=error)
@app.route('/send_video', methods=['GET', 'POST'])
@login_required
def send_video():
    error = None
    form = SendVideoForm()
    if request.method == "POST":
        if 'video' not in request.files:
            flash('No file part')
            error = "Video no presente"
            return redirect(url_for('index'))
        videoD = request.files['video']
        video = {'video': (videoD.filename, videoD)}
        #videoData = {'video': video.read()}
        REST_SERVER = os.environ.get('REST_SERVER', 'localhost')
        #url = 'http://'+REST_SERVER+':8080/rest/uploadVideo'
        url = 'http://'+REST_SERVER+':8080/rest/users/'+str(current_user.id)+'/videos/'
        if videoD.filename == '':
            flash('No image selected for uploading')
            error = "Video sin nombre"
            return redirect(url_for('index'))
        else :
            response = requests.post(
                        url,
                        files=video)
            if response.status_code == 200:
                error = "Status code 200"
                return render_template('send_video.html', form=form, error=error)
            else:
                error = response.status_code
                return render_template('send_video.html', form=form, error=error)
    return render_template('send_video.html', form=form, error=error)
@app.route('/profile')
@login_required
def profile():
    return render_template('profile.html')


@app.route('/logout')
@login_required
def logout():
    logout_user()
    return redirect(url_for('index'))


@login_manager.user_loader
def load_user(user_id):
    for user in users:
        if user.id == int(user_id):
            return user
    return None


if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0')
