from flask import Flask, render_template, send_from_directory, url_for, request, redirect
from flask_login import LoginManager, login_manager, current_user, login_user, login_required, logout_user
import os
# Usuarios
from models import users, User

# Login
from forms import LoginForm, RegisterForm
import requests
import json
import hashlib
app = Flask(__name__, static_url_path='')
login_manager = LoginManager()
login_manager.init_app(app)  # Para mantener la sesión

# Configurar el secret_key. OJO, no debe ir en un servidor git público.
# Python ofrece varias formas de almacenar esto de forma segura, que
# no cubriremos aquí.
app.config['SECRET_KEY'] = 'qH1vprMjavek52cv7Lmfe1FoCexrrV8egFnB21jHhkuOHm8hJUe1hwn7pKEZQ1fioUzDb3sWcNK1pJVVIhyrgvFiIrceXpKJBFIn_i9-LTLBCc4cqaI3gjJJHU6kxuT8bnC7Ng'


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
            # credenciales = {"email" : "dsevilla@um.es", "password" : "admin"}
            REST_SERVER = os.environ.get('REST_SERVER', 'localhost')
            credenciales = {"email":form.email.data, "password":form.password.data}
            response = requests.post("http://" + REST_SERVER + ":8080/rest/checkLogin", json=credenciales)
            # userName = form.email.data.encode('utf-8').split('@')[0]
            if (response.status_code == 200):
                # Tratar usuario devuelto en el response a través de json
                user = User.get_user(form.email.data.encode('utf-8'))
                if user is None:
                    user = User(int(response.json()['id']), response.json()['name'], form.email.data.encode('utf-8'), form.password.data.encode('utf-8'))  
                    users.append(user)
                    login_user(user, remember=form.remember_me.data)
                    return redirect(url_for('index'))
            else:
                error = response.status_code 
        return render_template('login.html', form=form, error=error)


@app.route('/register', methods=['GET', 'POST'])
def register():
    error = None
    REST_SERVER = os.environ.get('REST_SERVER', 'localhost')
    form = RegisterForm(request.form)
    if request.method == "POST" and form.validate_on_submit():
        credenciales = {"email":form.email.data, "username":form.username.data, "password":form.password.data}
        url = "http://" + REST_SERVER + ":8080/rest/register"
        #if request.method == 'POST' and 'username' in request.form and 'password' in request.form and 'email' in request.form:
        response = request.post(url, json=credenciales)
        if(response.status_code == 201):
            user = User(int(response.json()['id']['string']), form.username, form.email.data.encode('utf-8'),
                    form.password.data.encode('utf-8'))
            users.append(user)
            login_user(user, remember=form.remember_me.data)
            error = response.status_code
            return redirect(url_for('index'))
        elif (response.status_code == 409):
            error = response.status_code
            return redirect(url_for('index'))
        elif (response.status_code == 200):
            error = response.status_code
            return redirect(url_for('index'))
        else:
            error = response.status_code
            return redirect(url_for('index'))

    return render_template('register.html', form=form, error=error)


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
