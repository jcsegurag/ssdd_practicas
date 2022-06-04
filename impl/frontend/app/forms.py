from flask_wtf import FlaskForm
from wtforms import (StringField, PasswordField, BooleanField)
from wtforms.validators import DataRequired, Length

class LoginForm(FlaskForm):
    email = StringField('email', validators=[DataRequired()])
    password = PasswordField('password', validators=[DataRequired()])
    remember_me = BooleanField('Recuérdame')
    
class RegisterForm(FlaskForm):
    email = StringField('email', validators=[DataRequired()])
    username = StringField('username', validators=[DataRequired()])
    password = PasswordField('password', validators=[DataRequired()])
